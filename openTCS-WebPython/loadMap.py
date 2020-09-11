import xml.dom.minidom

def getDom():
    dom = xml.dom.minidom.parse("data/model.xml")
    root = dom.documentElement
    return root
    # print(root.nodeName)


def getPointDic(root):
    point_dic = {}
    points = root.getElementsByTagName("point")
    for point in points:
        # list_point_name = list(map(int, po))
        point_dic.update({point.getAttribute("name"): [point.getAttribute("xPosition"), point.getAttribute("yPosition"),
                                                       point.getAttribute("vehicleOrientationAngle"),
                                                       point.getAttribute("type")]})
        # print('point: {}, x:{}, y: {}, angle: {}, type: {}'.format(point.getAttribute("name"), point.getAttribute("xPosition"), point.getAttribute("yPosition"), point.getAttribute("vehicleOrientationAngle"), point.getAttribute("type")))
        paths = point.getElementsByTagName("outgoingPath")
        out_path_list = []
        for path in paths:
            out_path_list.append(path.getAttribute("name"))
            # print('outgoingPath: {}'.format(path.getAttribute("name")))
        point_dic[point.getAttribute("name")].append(out_path_list)
    return point_dic

def getPathDic(root):
    path_dic = {}
    return path_dic

def getLocationDic(root):
    locations_dic = {}
    locations = root.getElementsByTagName("location")
    aa = lambda x:x[0].getAttribute("point") if len(x) > 0 else ''
    for location in locations:
        link = location.getElementsByTagName("link")
        # link.
        locations_dic.update({location.getAttribute("name"): [location.getAttribute("xPosition"), location.getAttribute("yPosition"),
                                                       location.getAttribute("type"),[aa(link)]]})
    return locations_dic

class ModelMap:
    def __init__(self):
        self.dom = getDom()
        self.PointDic = getPointDic(self.dom)
        self.PathDic = getPathDic(self.dom)
        self.LocationDic = getLocationDic(self.dom)
    def getPoint(self):
        return self.PointDic


if __name__ == '__main__':
    map = ModelMap()
    print(map.PointDic)
    print(map.LocationDic)


