/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.simulator;

//import com.google.common.primitives.Ints;
//import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams.OrderResponse;
//import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams.StateResponse;
//import de.fraunhofer.iml.opentcs.example.common.telegrams.Telegram;

//import static com.google.common.base.Ascii.ETX;
//import static com.google.common.base.Ascii.STX;

/**
 * Represents the state of a physical vehicle.
 * Generally the content matches a {@link StateResponse}.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class VehicleState {

  /**
   * The telegram counter to match request and response.
   */
  private int telegramCounter;
  /**
   * The current operation mode of the vehicle.
   * (M)oving, (A)cting, (I)dle, (C)harging, (E)rror.
   */
  private char operationMode = 'M';
  /**
   * The load handling state of the vehicle.
   * (E)mpty, (F)ull, (U)nknown.
   */
  private char loadState = 'E';
  /**
   * The id of the current position.
   */
  private int positionId;
  /**
   * The order id of the last finished order.
   */
  private int lastFinishedOrderId;
  /**
   * The order id of the currently executed order.
   */
  private int currOrderId;
  /**
   * The order id of the last received oder.
   */
  private int lastReceivedOrderId;

  public int getTelegramCounter() {
    return telegramCounter;
  }

  public void setTelegramCounter(int telegramCounter) {
    this.telegramCounter = telegramCounter;
  }

  public char getOperationMode() {
    return operationMode;
  }

  public void setOperationMode(char operationMode) {
    this.operationMode = operationMode;
  }

  public char getLoadState() {
    return loadState;
  }

  public void setLoadState(char loadState) {
    this.loadState = loadState;
  }

  public int getPositionId() {
    return positionId;
  }

  public void setPositionId(int positionId) {
    this.positionId = positionId;
  }

  public int getLastFinishedOrderId() {
    return lastFinishedOrderId;
  }

  public void setLastFinishedOrderId(int lastFinishedOrderId) {
    this.lastFinishedOrderId = lastFinishedOrderId;
  }

  public int getCurrOrderId() {
    return currOrderId;
  }

  public void setCurrOrderId(int currOrderId) {
    this.currOrderId = currOrderId;
  }

  public int getLastReceivedOrderId() {
    return lastReceivedOrderId;
  }

  public void setLastReceivedOrderId(int lastReceivedOrderId) {
    this.lastReceivedOrderId = lastReceivedOrderId;
  }

  /**
   * Creates a state response for the current vehicle state
   *
   * @return A state response
   */
  public StateResponse toStateResponse() {
    byte[] telegramData = new byte[StateResponse.TELEGRAM_LENGTH];

    telegramData[0] = STX;
    telegramData[1] = StateResponse.PAYLOAD_LENGTH;
    telegramData[2] = StateResponse.TYPE;
    // set telegram counter
    byte[] tmp = Ints.toByteArray(getTelegramCounter());
    telegramData[3] = tmp[2];
    telegramData[4] = tmp[3];
    // set pos id
    tmp = Ints.toByteArray(getPositionId());
    telegramData[5] = tmp[2];
    telegramData[6] = tmp[3];
    // set op mode
    telegramData[7] = (byte) getOperationMode();
    // set load state
    telegramData[8] = (byte) getLoadState();
    // set last received order id
    tmp = Ints.toByteArray(getLastReceivedOrderId());
    telegramData[9] = tmp[2];
    telegramData[10] = tmp[3];
    // set current order id
    tmp = Ints.toByteArray(getCurrOrderId());
    telegramData[11] = tmp[2];
    telegramData[12] = tmp[3];
    // set last finished order id
    tmp = Ints.toByteArray(getLastFinishedOrderId());
    telegramData[13] = tmp[2];
    telegramData[14] = tmp[3];
    // set checksum
    telegramData[StateResponse.CHECKSUM_POS] = Telegram.getCheckSum(telegramData);
    telegramData[StateResponse.TELEGRAM_LENGTH - 1] = ETX;

    telegramCounter++;

    return new StateResponse(telegramData);
  }

  /**
   * Creates an order response for the current vehicle state.
   *
   * @return The order response
   */
  public OrderResponse toOrderResponse() {
    byte[] telegramData = new byte[OrderResponse.TELEGRAM_LENGTH];

    telegramData[0] = STX;
    telegramData[1] = OrderResponse.PAYLOAD_LENGTH;
    telegramData[2] = OrderResponse.TYPE;
    // set telegram counter
    byte[] tmp = Ints.toByteArray(getTelegramCounter());
    telegramData[3] = tmp[2];
    telegramData[4] = tmp[3];
    // set last received order id
    tmp = Ints.toByteArray(getLastReceivedOrderId());
    telegramData[5] = tmp[2];
    telegramData[6] = tmp[3];
    // set checksum
    telegramData[OrderResponse.CHECKSUM_POS] = Telegram.getCheckSum(telegramData);
    telegramData[OrderResponse.TELEGRAM_LENGTH - 1] = ETX;

    telegramCounter++;

    return new OrderResponse(telegramData);
  }
}
