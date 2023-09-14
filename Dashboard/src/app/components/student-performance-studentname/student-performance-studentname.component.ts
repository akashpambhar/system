import { Component, OnInit } from '@angular/core';
import {ReportService} from "../../services/report.service";
import {WebSocketService} from "../../services/web-socket.service";

@Component({
  selector: 'app-student-performance-studentname',
  templateUrl: './student-performance-studentname.component.html',
  styleUrls: ['./student-performance-studentname.component.scss']
})
export class StudentPerformanceStudentnameComponent implements OnInit {

  studentName: string = '';
  response: any;

  constructor(private reportService: ReportService,
              private webSocketService: WebSocketService) { }

  onSubmit() {
    this.reportService.getStudentPerformanceByStudentName(this.studentName);
  }

  ngOnInit(): void {
    this.webSocketService.subscribeReport('/topic/student-performance-studentname', (data: any) => {
      data = JSON.parse(data.body)
      this.response = data.object;
    })
  }

}
