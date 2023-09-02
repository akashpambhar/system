import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { Student } from './student.interface';
import { StudentService } from 'src/app/services/student.service';
import { ActivatedRoute } from '@angular/router';
import { WebSocketService } from 'src/app/services/web-socket.service';

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.scss']
})
export class StudentComponent implements OnInit {

  selectedStudent: Student | undefined;
  studentId = "";

  barChartOptions: ChartConfiguration['options'] = {
    responsive: true
  };
  barChartType: ChartType = 'bar';
  barChartLegend: boolean = true;
  barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      { data: [], label: '' }
    ],
  };

  constructor(private studentService: StudentService, private route: ActivatedRoute, private cd: ChangeDetectorRef,
    private webSocketService: WebSocketService) { }

  ngOnInit(): void {
    this.showMarks();

    this.webSocketService.subscribe('/topic/chart-data', (data: any) => {
      data = JSON.parse(data.body)
  
      const labels = this.barChartData.labels
      labels?.push(this.formatDateTime(data.creationDate))
      const da = this.barChartData.datasets[0].data
      da.push(data.studentCount);

      const updatedChartData: ChartData<'bar'> = {
        labels: labels,
        datasets: [{ data: da, label: 'Students' }]
      };

      this.barChartData = updatedChartData
    })
  }

  showMarks(): void {
    this.studentService.getChartData().subscribe(chartData => {
      const labels: string[] = [];
      const data: number[] = [];

      for (const d of chartData) {
        labels.push(this.formatDateTime(d.creationDate));
        data.push(d.studentCount);
      }

      const updatedChartData: ChartData<'bar'> = {
        labels: labels,
        datasets: [{ data: data, label: 'Students' }]
      };

      this.barChartData = updatedChartData;
    });
  }

  formatDateTime(d: string) {    
    const timestamp: Date = new Date(d);

    const date: string = timestamp.toISOString().split("T")[0];
    const time: string = timestamp.toTimeString().split(" ")[0];

    return `${date} ${time}`
  }


}
