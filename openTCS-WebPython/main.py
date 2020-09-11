import PySimpleGUI as sg
import loadMap

v = [['名称','电量','状态','位置','订单目标点'], ['Vehicle-01', '80', '等待', 'B', '无']]

layout = [
    # [sg.Text('OpenTCS Web Client', size=(120,1), font=('Comix san ms', 20), text_color='black')],
    [sg.Table(values=v)],
    [sg.Button('取消订单',button_color=('red')), sg.Button('下单', button_color=('green'))]
]


def app():
    window = sg.Window('OpenTCS Web Client', layout)
    while True:
        event, values = window.read(timeout=1)

if __name__ == "__main__":
    map = loadMap.ModelMap()
    print(map.PointDic)
    app()

