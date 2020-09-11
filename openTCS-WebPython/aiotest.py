import socket
import asyncio

# class TcpClient(asyncio.Protocal):
async def tcp_echo_client(message, loop):
    reader, writer = await asyncio.open_connection('192.168.0.37', 2300,
                                                   loop=loop)

    # print('Send: %r' % message)
    # writer.write(message.encode())
    while 1:
        data = await reader.read(100)
        if(len(data) == 18):
            data.strip()
            x = float(data[2:7])
            y = float(data[10:16])
            print("data leng %r" % len(data))
            print('X: {}, Y: {}'.format(x,y))
        await asyncio.sleep(0.01)

    # print('Close the socket')
    # writer.close()
async def handle_echo(reader, writer):
    # while 1:
    data = await reader.read(100)
    message = data.decode()
    addr = writer.get_extra_info('peername')
    print("Received %r from %r" % (message, addr))

    # print("Send: %r" % message)
    # writer.write(data)
    # await writer.drain()
    # await asyncio.sleep(0.1)

    # print("Close the client socket")
    # writer.close()

# async def tcp_server(loop):
    # server = loop.run_until_complete(coro)


message = 'Hello World!'
loop = asyncio.get_event_loop()
coro = asyncio.start_server(handle_echo, '127.0.0.1', 28888, loop=loop)
loop.run_until_complete(tcp_echo_client(message, loop))
# loop.run_until_complete(coro)
# loop.close()
try:
	loop.run_forever()
except KeyboardInterrupt:
	pass