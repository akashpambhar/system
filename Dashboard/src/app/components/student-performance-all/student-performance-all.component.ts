import { Component, OnInit } from '@angular/core';
import {ReportService} from "../../services/report.service";
import {WebSocketService} from "../../services/web-socket.service";

@Component({
  selector: 'app-student-performance-all',
  templateUrl: './student-performance-all.component.html',
  styleUrls: ['./student-performance-all.component.scss']
})
export class StudentPerformanceAllComponent implements OnInit {

  response: any;

  constructor(private reportService: ReportService,
              private webSocketService: WebSocketService) { }

  ngOnInit(): void {
    this.reportService.getAllStudentPerformance()

    this.webSocketService.subscribeReport('/topic/student-performance-all', (data: any) => {
      data = JSON.parse(data.body)
      this.response = data.object;
    })
  }
}