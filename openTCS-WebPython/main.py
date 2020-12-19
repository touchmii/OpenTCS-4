# import PySimpleGUI as sg
import PySimpleGUIWeb as sg
import loadMap
import webapi
import threading
import asyncio

v = [['名称','电量','状态','位置','订单目标点'], ['Vehicle-01', '80', '等待', 'B', '无'], ['Vehicle-02', '60', '等待', 'L3', '无']]
vv = [['订单ID', '目标点', '动作', '执行状态'], ['1', 'L3', '载货', '执行完成'], ['2', 'L2', '卸货', '执行中'], ['3', 'L3', '载货', '未分配'], ['5', 'L2', '载货', '未分配']]
# vvv = [['订单ID', '目标点', '动作'], ['38', 'L9', '载货'], ['38', 'L9', '载货']]
vvv = [['订单ID', '目标点', '动作']]
# map = loadMap.ModelMap()
#
# xxx = list(map.PointDic.keys())
tcs = webapi.opentcs("127.0.0.1")
vehicles = tcs.get_vehicles_status()
locations = tcs.get_locations()
points = tcs.get_points()
paths = tcs.get_paths()
xxx = [i['name'] for i in locations]
points_pose = [[i['name'],i['position']['x']/200,i['position']['y']/200] for i in points]
points_dic = dict(zip([i[0] for i in points_pose], [i[1:] for i in points_pose]))
path_list = [[i['sourcePoint']['name'], i['destinationPoint']['name']] for i in paths]
# print(path_list)
print(xxx)
# print(points_pose)
graph_elem = sg.Graph((400, 200), (-10, -20), (390, 180),
                      enable_events=True, key='_GRAPH_', background_color='lightblue')
vehicle_graph = None
vehicle_path_graph = None
vehicle_current_order = None

layout = [
    [graph_elem],
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
    global vehicle_graph
    global vehicle_path_graph
    global vehicle_current_order
    window = sg.Window('OpenTCS Web Client', layout, web_ip='0.0.0.0', web_port=8089, finalize=True)
    window.Finalize()
    for i in points_pose:
        graph_elem.draw_circle((i[1],i[2]), 1, fill_color='black', line_color='red')
    for i in path_list:
        graph_elem.draw_line(points_dic[i[0]], points_dic[i[1]], color='black', width=1)
    for i in vehicles:
        vehicle_graph = graph_elem.draw_circle(points_dic[i[-2]], 5, fill_color='green', line_color='green')
    # draw_order()
    action_dic = {'无':"NOP", '卸货':"UNLOAD",'载货':'LOAD', '充电':'CHARGE'}
    while True:
        event, values = window.read(timeout=1)
        if event == '添加':
            vvv.append([len(vvv),values['Target'], values['action']])
            window['transport'].Update(values=vvv)
        if event == '下单':
            print('下单'+values['Target']+values['action'])
            tcs.creat_order(locations=[[values['Target'], action_dic[values['action']]]])

def draw_order():
    global vehicle_current_order
    vehicles = tcs.get_vehicles_status()
    pose = points_dic[vehicles[0][-2]]
    # if vehicles[0][-3] != vehicle_current_order:
        # if vehicle_path_graph != None:
        #     for i in vehicle_path_graph:
        #         graph_elem.DeleteFigure(i)
    vehicle_current_order = vehicles[0][-3]
    current_order = tcs.get_driver_orders_byname(vehicles[0][-3])
    steps = current_order['route']['steps']
    current_path_list = [ [i['sourcePoint']['name'], i['destinationPoint']['name']] for i in steps]
    # vehicle_path_graph = graph_elem
    for i in current_path_list:
        # vehicle_path_graph.append(graph_elem.draw_line(points_dic[i[0]], points_dic[i[1]], color='blue', width=1))
        graph_elem.draw_line(points_dic[i[0]], points_dic[i[1]], color='blue', width=3)
async def hello():
    global vehicle_current_order
    global vehicle_path_graph
    global graph_elem
    while True:
        # print('xx')

        vehicles = tcs.get_vehicles_status()
        pose = points_dic[vehicles[0][-2]]
        if vehicles[0][-3] != vehicle_current_order:
            if vehicle_path_graph != None:
                for i in vehicle_path_graph:
                    graph_elem.DeleteFigure(i)
            vehicle_current_order = vehicles[0][-3]
            current_order = tcs.get_driver_orders_byname(vehicles[0][-3])
            steps = current_order['route']['steps']
            current_path_list = [ [i['sourcePoint']['name'], i['destinationPoint']['name']] for i in steps]
            # vehicle_path_graph = graph_elem
            for i in current_path_list:
                # vehicle_path_graph.append(graph_elem.draw_line(points_dic[i[0]], points_dic[i[1]], color='blue', width=1))
                graph_elem.draw_line(points_dic[i[0]], points_dic[i[1]], color='blue', width=3)
        graph_elem.RelocateFigure(vehicle_graph, pose[0], pose[1])
        await asyncio.sleep(1)
if __name__ == "__main__":

    threading.Thread(target=app).start()
    # app()
    loop = asyncio.get_event_loop()
    loop.run_until_complete(hello())
    # tasks = [asyncio.Task(run_server(loop)), asyncio.Task(joystick()), asyncio.Task(detect_joystick())]
    # tasks = [asyncio.Task(app()), asyncio.Task(hello())]
    # loop.run_until_complete(asyncio.wait(tasks))
    # hello()

