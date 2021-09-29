# coding: utf-8
# usr/bin/python3

import asyncio
# import mapxslfast as maps
import random
from queue import Queue

query = b'\x00\x01\x02\x01\xfd'
query_r = bytearray(
    b'\x00\x01\x82\x00\x00\x00\x01\x0a\x00\x04\x00\x1a\x00\x88\x50\x0f\x00\xbe')
query_re = b'\x00\x01\x82\x00\x00\x00\x00\xc3\x00\x04\x00\x1a\x00\x88\x50\x0f\x00\xbe'
# query_re = b'\x00\x01\x82\x00\x00\x00\x00\xc3'
agv1_id = 204
agv2_id = 208
agv1_path = [204]
agv2_path = []
agv2_path_queue = Queue()
path_index = 0


def from_bytes(data, big_endian=False):
    if isinstance(data, str):
        data = bytearray(data)
    if big_endian:
        data = reversed(data)
    num = 0
    for offset, byte in enumerate(data):
        num += byte << (offset * 8)
    return num


def depath(path):
    path_list = []
    path = path[9:-1]
    for i in range(int(len(path)/4)):
        j = i*4
        k = j+2
        path_list.append(from_bytes(path[j:k], True))
    return path_list


async def handle_echo(reader, writer):
    query_re = query_r
    global agv1_id
    global agv2_id
    global agv1_path
    global agv2_path
    global agv2_path_queue
    global path_index
    while True:
        data = await reader.read(512)
        if data == b'':
            continue
        # message = data.decode()
        message = data
        addr = writer.get_extra_info('peername')
        # print("Received %r from %r" % (message, addr))
        if message == query:
            if addr[1] < 10000:
                query_re[6] = int(agv1_id/256)
                query_re[7] = int(agv1_id % 256)
                writer.write(query_re)
                await writer.drain()
                print("Send: %r" % query_re)
            elif addr[1] < 20000 and addr[1] > 10000:
                query_re[6] = int(agv2_id/256)
                query_re[7] = int(agv2_id % 256)
                writer.write(query_re)
                await writer.drain()
                print("Send: %r" % query_re)
            else:
                if agv2_path_queue.empty():
                    query_re[12] = 0
                else:
                    query_re[12] = 1
                query_re[6] = int(agv2_id/256)
                query_re[7] = int(agv2_id % 256)
                writer.write(query_re)
                await writer.drain()
#                 print("Send: %r" % query_re)
        elif message[:3] == b'\x00\x01\x0c':
            path_list = depath(message)
#			print(path_list)
            if addr[1] < 10000:
                agv1_path = path_list
#				print(path_list)
                query_re[6] = int(agv1_id/256)
                query_re[7] = int(agv1_id % 256)
                writer.write(query_re)
                await writer.drain()
                print("Send: %r" % query_re)
#			path_list = depath(message)
            elif addr[1] < 20000 and addr[1] > 10000:
                agv2_path = path_list
                print(agv2_path)
                query_re[6] = int(agv2_id/256)
                query_re[7] = int(agv2_id % 256)
                writer.write(query_re)
                await writer.drain()
                print("Send: %r" % query_re)
            else:
                print(path_list)
                agv2_path = path_list
                path_index = 0
                # for i in path_list:
                #     agv2_path_queue.put(i)
                # print(path_list)
                query_re[6] = int(agv2_id/256)
                query_re[7] = int(agv2_id % 256)
                writer.write(query_re)
                await writer.drain()
                print("Send: %r" % query_re)
        await asyncio.sleep(.2)


async def sim_path(agv_num):
    global agv1_id
    global agv2_id
    global agv1_path
    global agv2_path
    if agv_num == 1:
        while True:
            if len(agv1_path) > 2:
                for i in agv1_path:
                    await asyncio.sleep(1)
                    agv1_id = i
                agv1_path = [agv1_id]
            await asyncio.sleep(.2)
    elif agv_num == 2:
        global agv2_path_queue
        path_index = 0
        while True:
            # 			if len(agv2_path) > 2:
            # 				for i in agv2_path:
            # 					await asyncio.sleep(1)
            # 					agv2_id = i
            # 					print('agv id: %r' % agv2_id)
            # 				agv2_path = [agv2_id]
            # if not agv2_path_queue.empty:
            #     agv2_id = await agv2_path_queue.get()
            #     print(agv2_id)
            if len(agv2_path) > 0 and path_index < len(agv2_path):
                agv2_id = agv2_path[path_index]
                path_index = path_index + 1
                print(agv2_id)
            await asyncio.sleep(3)
#		writer.write(query_re)
#		await writer.drain()

            # print("Close the client socket")
            # writer.close()
# class agv_sim():

loop = asyncio.get_event_loop()
coro = asyncio.start_server(handle_echo, '0.0.0.0', 10001, loop=loop)
# coro2 = asyncio.start_server(handle_echo, '0.0.0.0', 10002, loop=loop)
# server = loop.run_until_complete(coro)

# loop.run_until_complete(coro)
pq = Queue()

tasks = [asyncio.Task(coro), asyncio.Task(
    sim_path(1)), asyncio.Task(sim_path(2))]
loop.run_until_complete(asyncio.wait(tasks))
# server2 = loop.run_until_complete(coro2)

# Serve requests until Ctrl+C is pressed
# print('Serving on {}'.format(server.sockets[0].getsockname()))
try:
    loop.run_forever()
except KeyboardInterrupt:
    pass

# Close the server
# server.close()
# loop.run_until_complete(server.wait_closed())
# loop.close()
