#!/usr/bin/env python3

"""A simple HTTP server with REST and json for python 3.
addrecord takes utf8-encoded URL parameters
getrecord returns utf8-encoded json.
"""

from http.server import BaseHTTPRequestHandler, HTTPServer
import argparse
import re
import cgi
import json
import threading
import asyncio
import time
from queue import Queue
from urllib import parse
from pymodbus.client.sync import ModbusSerialClient as ModbusClient
import serial


position = [0,0]

class LocalData(object):
    records = {}
    qu = Queue(100)


class HTTPRequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if re.search('/api/v1/addrecord/*', self.path):
            ctype, pdict = cgi.parse_header(
                self.headers.get('content-type'))
            if ctype == 'application/json':
                length = int(self.headers.get('content-length'))
                rfile_str = self.rfile.read(length).decode('utf8')
                data = parse.parse_qs(rfile_str, keep_blank_values=1)
                record_id = self.path.split('/')[-1]
                LocalData.records[record_id] = data
                print("addrecord %s: %s" % (record_id, data))
                # HTTP 200: ok
                self.send_response(200)
            else:
                # HTTP 400: bad request
                self.send_response(400, "Bad Request: must give data")
        elif re.search('/v1/vehicle/sendpath', self.path):
            ctype, pdict = cgi.parse_header(self.headers.get('content-type'))
            if ctype == 'application/json':
                length = int(self.headers.get('content-length'))
                rfile_str = self.rfile.read(length).decode('utf8')
                data = parse.parse_qs(rfile_str, keep_blank_values=1)
                record_id = self.path.split('/')[-1]
                LocalData.records[record_id] = data
                path = json.loads(rfile_str)
                # global sim
                # sim.add_path(path)
                LocalData.qu.put(path)
                print("addrecord %s: %s" % (record_id, data))
                # HTTP 200: ok
                self.send_response(200)
                self.send_header('Content-Type', 'application/json')
                self.end_headers()
                data =json.dumps({"status":"ok"})
                self.wfile.write(data.encode('utf8'))
        else:
            # HTTP 403: forbidden
            self.send_response(403)

        self.end_headers()

    def do_GET(self):
        if re.search('/api/v1/shutdown', self.path):
            # Must shutdown in another thread or we'll hang
            def kill_me_please():
                self.server.shutdown()
            threading.Thread(target=kill_me_please).start()

            # Send out a 200 before we go
            self.send_response(200)
        elif re.search('/api/v1/getrecord/*', self.path):
            record_id = self.path.split('/')[-1]
            if record_id in LocalData.records:
                self.send_response(200)
                self.send_header('Content-Type', 'application/json')
                self.end_headers()
                # Return json, even though it came in as POST URL params
                data = json.dumps(LocalData.records[record_id])
                print("getrecord %s: %s" % (record_id, data))
                self.wfile.write(data.encode('utf8'))
            else:
                self.send_response(404, 'Not Found: record does not exist')
        elif re.search('/v1/vehicle/status', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            data =json.dumps({"x":position[0],"y":position[1],"status":"idle","battery":50,"fork":"unload"})
            self.wfile.write(data.encode('utf8'))
        else:
            self.send_response(403)

        self.end_headers()
current_position = [0,0]
class sim_vehicle:
    def __init__(self, sim_time=0.1):
        self.sim_time = sim_time
        global current_position
        self.target_position = [0,0]
        self.position_queue = Queue(100)
    def set_sim_time(self, new_sim_time):
        self.sim_time = new_sim_time
    def add_path(self, new_path):
        for path_point in new_path:
            self.position_queue.put(path_point)
    def get_path(self):
        # pass
        return list(self.position_queue.queue)
    async def start_simulate(self):
        while True:
            await asyncio.sleep(self.sim_time)
            # print(",\n")
            print(LocalData.qu.qsize())
            if self.target_position != current_position:
                if self.target_position[0] - current_position[0] > 10:
                    pass

async def get_pose():
    while True:
        await asyncio.sleep(0.2)
        #获取串口数据代码


def main():
    parser = argparse.ArgumentParser(description='HTTP Server')
    parser.add_argument('port', type=int, help='Listening port for HTTP Server')
    parser.add_argument('ip', help='HTTP Server IP')
    args = parser.parse_args()

    server = HTTPServer((args.ip, args.port), HTTPRequestHandler)
    print('HTTP Server Running...........')
    # server.serve_forever()
    threading.Thread(target=server.serve_forever).start()
    loop = asyncio.get_event_loop()
#    sim = sim_vehicle()
    loop.run_until_complete(get_pose())
    pp = []
    for i in range(10):
        pp.append([i,i*2])
    sim.add_path(pp)
    print(sim.get_path())
    #28H 测距定位使能:0:不测距 1 单次测量 2 持续测量 3 单 次自动输出 4 持续自动输出
#    client = ModbusClient(method='rtu', port='COM1', timeout=1, baudrate=115200)
#    client.connect()
#    client.write_register(0x28, 2)

    serial = serial.Serial("COM3",115200)
    serial.open()
    serial.write([0x01,0x10,0x00,0x28,0x00,0x01,0x02,0x00,0x02,0x21,0xB9])
    n = serial.inWaiting()
    time.sleep(0.1)
    serial.read(n)


    while True:
        #0x2C标签X坐标,0x2D Y坐标
#        request = client.read_input_registers(0x2C, 2)
        # request = client.read_holding_registers(0x2C, 2)
        n2 = serial.inWaiting()
        serial.write([0x01,0x03,0x00,0x2A,0x00,0x0D,0xA5,0xC7])
        time.sleep(0.1)
        rec_data = serial.read(n2)
        rec_data[7:9]
        position[0] = request.registers[0]
        position[1] = request.registers[1]
        print('positon: x {}, y{}'.format(positon[0], positon[1]))
        time.sleep(0.1)



if __name__ == '__main__':
    main()