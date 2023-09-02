import { Injectable } from '@angular/core';
import { WebSocketSubject, webSocket } from 'rxjs/webSocket';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private socket = SockJS('http://localhost:8080/websocket');
  stompClient = Stomp.over(this.socket);

  constructor() {
  }

  subscribe(topic: string, callback: any) {
    this.stompClient.connect({}, (): any => {
      this.subscribeToTopic(topic, callback);
    })
  }

  private subscribeToTopic(topic: string, callback: any) {
    this.stompClient.subscribe(topic, (chartData) => {
      callback(chartData)
    })
  }
}
