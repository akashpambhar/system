import { Component, OnInit } from '@angular/core';
import { ReportService } from 'src/app/services/report.service';
import { WebSocketService } from 'src/app/services/web-socket.service';

@Component({
  selector: 'app-student-performance-classname',
  templateUrl: './student-performance-classname.component.html',
  styleUrls: ['./student-performance-classname.component.scss']
})
export class StudentPerformanceClassnameComponent implements OnInit {

  className: string = '';
  response: any;

  constructor(private reportService: ReportService,
    private webSocketService: WebSocketService) { }

  onSubmit() {
    this.reportService.getStudentPerformanceByClassName(this.className);
  }

  ngOnInit(): void {
    this.webSocketService.subscribeReport('/topic/student-performance-classname', (data: any) => {
      data = JSON.parse(data.body)
      this.response = data.object;
    })
  }

}
