# import PySimpleGUI as sg
import PySimpleGUIWeb as sg
import loadMap
import webapi

v = [['名称','电量','状态','位置','订单目标点'], ['Vehicle-01', '80', '等待', 'B', '无'], ['Vehicle-02', '60', '等待', 'L3', '无']]
vv = [['订单ID', '目标点', '动作', '执行状态'], ['1', 'L3', '载货', '执行完成'], ['2', 'L2', '卸货', '执行中'], ['3', 'L3', '载货', '未分配'], ['5', 'L2', '载货', '未分配']]
# vvv = [['订单ID', '目标点', '动作'], ['38', 'L9', '载货'], ['38', 'L9', '载货']]
vvv = [['订单ID', '目标点', '动作']]
# map = loadMap.ModelMap()
#
# xxx = list(map.PointDic.keys())
tcs = webapi.opentcs("127.0.0.1")
locations = tcs.get_locations()
xxx = [i['name'] for i in locations]

layout = [
    [sg.Text('车辆状态:', size=(10, 1), font=('Comix san ms', 16), text_color='black')],
    [sg.Table(values=v, col_widths=[4, 4, 4, 4, 10], auto_size_columns=0, num_rows=4, display_row_numbers=0, justification='center', text_color='black')],
    [sg.Text('订单状态:', size=(10, 1), font=('Comix san ms', 16), text_color='black')],
    [sg.Table(values=vv, col_widths=[6, 6, 4, 8], auto_size_columns=0, select_mode=sg.TABLE_SELECT_MODE_BROWSE, num_rows=2, display_row_numbers=False, justification='center', text_color='black')],
    [sg.Button('取消订单'), sg.Button('暂停订单'), sg.Button('继续订单')],
    [sg.Text('新建订单:', size=(10, 1), font=('Comix san ms', 16), text_color='black')],
    [sg.Text('目标点', size=(6,1)), sg.Combo(xxx, size=(6, 1), auto_size_text=False, enable_events=True, key='Target'), sg.Text('动作: '), sg.Combo(['无', '载货', '卸货', '充电'], size=(7, 1), enable_events=True, key='action'), sg.Button('添加')],
    [sg.Table(values=vvv, col_widths=[5, 6, 4], auto_size_columns=0, num_rows=4, display_row_numbers=0, justification='center', text_color='black', key='transport')],
    [sg.Button('取消订单',button_color=('red')), sg.Button('下单', button_color=('green'))]
]



def app():
    # global vvv

    # print(xxx)

    window = sg.Window('OpenTCS Web Client', layout, web_ip='0.0.0.0', web_port=8089)
    action_dic = {'无':"NOP", '卸货':"UNLOAD",'载货':'LOAD', '充电':'CHARGE'}
    while True:
        event, values = window.read(timeout=1)
        if event == '添加':
            vvv.append([len(vvv),values['Target'], values['action']])
            window['transport'].Update(values=vvv)
        if event == '下单':
            print('下单'+values['Target']+values['action'])
            tcs.creat_order(locations=[[values['Target'], action_dic[values['action']]]])

if __name__ == "__main__":

    app()

