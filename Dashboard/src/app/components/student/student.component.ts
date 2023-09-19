import { Component, OnInit } from '@angular/core';

import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { Student } from './student.interface';
import { StudentService } from 'src/app/services/student.service';
import { WebSocketService } from 'src/app/services/web-socket.service';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
  styleUrls: ['./student.component.scss']
})
export class StudentComponent implements OnInit {

  selectedStudent: Student | undefined;
  studentId = "";
  responseUploadData:any

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

  selectedFile: File | null = null;
  isSubscribed = false;
  isSubscribedReport = false;

  constructor(private studentService: StudentService,
    private webSocketService: WebSocketService,
    private authService: AuthService,
    private router: Router) { }

  ngOnInit(): void {
    this.showMarks();
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

  selectFile(event: any) {
    this.selectedFile = event.target.files.item(0);
  }

  onSubmit() {
    if (!this.selectedFile) {
      alert('Please select a file to upload.');
      return;
    }

    if(!this.isSubscribed){
      this.subscribe();
    }

    this.studentService.uploadFile(this.selectedFile)
      .subscribe(
        (response) => {
          console.log('File upload successful:', response);
        },
        (error) => {
          console.error('File upload failed:', error);
        }
      );
  }

  getUserReport(){
    if(!this.isSubscribedReport){
      this.subscribeToReport();
    }
    
    this.studentService.getUserReport().subscribe(data => {
      console.log(data)
    });
  }

  subscribe(){
    this.webSocketService.subscribe('/topic/chart-data', (data: any) => {
      data = JSON.parse(data.body)
      this.isSubscribed = true;

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

  subscribeToReport(){
    this.webSocketService.subscribe('/topic/upload-data', (data: any) => {
      this.responseUploadData = JSON.parse(data.body)
    })
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.removeToken();
      this.router.navigate(['/']);
    })
  }
}

