import { Component, OnInit } from '@angular/core';
import { WebSocketService } from 'src/app/services/web-socket.service';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {

  selectedElement: string = 'select-report-type';

  constructor(private webSocketService: WebSocketService) { }

  ngOnInit(): void {

  }

}
