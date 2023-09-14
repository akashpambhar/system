import { Component, OnInit } from '@angular/core';
import { ReportService } from 'src/app/services/report.service';
import { WebSocketService } from 'src/app/services/web-socket.service';

@Component({
  selector: 'app-school-performance-classname-subjectname',
  templateUrl: './school-performance-classname-subjectname.component.html',
  styleUrls: ['./school-performance-classname-subjectname.component.scss']
})
export class SchoolPerformanceClassnameSubjectnameComponent implements OnInit {

  className: string = '';
  subjectName: string = '';
  response: any;

  constructor(private reportService: ReportService,
    private webSocketService: WebSocketService) { }

  onSubmit() {
    this.reportService.getSchoolPerformanceByClassNameSubjectName(this.className, this.subjectName);
  }

  ngOnInit(): void {
    this.webSocketService.subscribeReport('/topic/school-performance-classname-subjectname', (data: any) => {
      data = JSON.parse(data.body)
      this.response = data.object;
      this.response = this.response.map((item: any) => ({
        ...item,
        creationDate: new Date(
          item.creationDate[0],
          item.creationDate[1] - 1, // Subtract 1 from the month since months are 0-based
          item.creationDate[2],
          item.creationDate[3],
          item.creationDate[4],
          item.creationDate[5],
          item.creationDate[6]
        )
      }));
    })
  }
}
