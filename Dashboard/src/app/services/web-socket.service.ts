import { Injectable } from '@angular/core';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private socket = SockJS('http://localhost:8080/websocket');

  private socketReport = SockJS('http://localhost:1010/websocket');

  stompClient = Stomp.over(this.socket);
  stompClientReport = Stomp.over(this.socketReport);

  constructor() {
    if(!this.stompClient.connected)
      this.stompClient.connect({}, (): any => { })
    
    if(!this.stompClientReport.connected)
      this.stompClientReport.connect({}, (): any => { })
  }

  subscribe(topic: string, callback: any) {
    this.stompClient.subscribe(topic, (data) => {
      callback(data)
    })
  }

  subscribeReport(topic: string, callback: any) {
    this.stompClientReport.subscribe(topic, (data) => {
      callback(data)
    })
  }
}
