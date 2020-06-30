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

public class ModbusProtocoll 
{
    public java.util.Calendar timeStamp;
    public boolean request;
    public boolean response;
    public int transactionIdentifier;
    public int protocolIdentifier;
    public int length;
    public byte unitIdentifier;
    public byte functionCode;
    public int startingAdress;
    public int quantity;
    public short byteCount;
    public byte exceptionCode;
    public byte errorCode;
    public short[] receiveCoilValues;
    public int[] receiveRegisterValues;
    public int[] sendRegisterValues;
    public boolean[] sendCoilValues;
}

