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

import java.io.IOException;

class ListenerThread extends Thread 
{
	ModbusServer easyModbusTCPServer;
	public ListenerThread(ModbusServer easyModbusTCPServer)
	{
		this.easyModbusTCPServer = easyModbusTCPServer;
	}
	
	  public void run()
	    {		  
		  java.net.ServerSocket serverSocket = null;
		try {
             	serverSocket = new java.net.ServerSocket(easyModbusTCPServer.getPort());

	    
	        while (easyModbusTCPServer.getServerRunning() & !this.isInterrupted())
	        {	
                    java.net.Socket socket = serverSocket.accept();
                    (new ClientConnectionThread(socket, easyModbusTCPServer)).start();
	        }
		} catch (IOException e) {
			System.out.println(e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		if (serverSocket != null)
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

}
