/*
 * (c) Stefan Roﬂmann
 *	This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.re.easymodbus.modbusserver;


class ClientConnectionThread extends Thread 
{
	private java.net.Socket socket;
	private byte[] inBuffer = new byte[1024];
	ModbusServer easyModbusTCPServer;
	
	public ClientConnectionThread(java.net.Socket socket, ModbusServer easyModbusTCPServer)
	{
		this.easyModbusTCPServer = easyModbusTCPServer;
		this.socket = socket;		
	}
	
	public void run()
	{
            this.easyModbusTCPServer.setNumberOfConnectedClients(this.easyModbusTCPServer.getNumberOfConnectedClients()+1);
            
            try
            {
                socket.setSoTimeout(easyModbusTCPServer.getClientConnectionTimeout());
                java.io.InputStream inputStream;                   
                inputStream = socket.getInputStream();
                while (socket.isConnected() & !socket.isClosed() & easyModbusTCPServer.getServerRunning())
		{
                	
                    int numberOfBytes=(inputStream.read(inBuffer));
                    if (numberOfBytes  > 4)
                    (new ProcessReceivedDataThread(inBuffer, easyModbusTCPServer, socket)).start();
                    Thread.sleep(5);
		}
                this.easyModbusTCPServer.setNumberOfConnectedClients(this.easyModbusTCPServer.getNumberOfConnectedClients()-1);
                socket.close();
            } catch (Exception e) 
            {
                this.easyModbusTCPServer.setNumberOfConnectedClients(this.easyModbusTCPServer.getNumberOfConnectedClients()-1);
                try
                {
                 socket.close();
                }
                catch (Exception exc)
                {}
                e.printStackTrace();
            }
	}
	
}
