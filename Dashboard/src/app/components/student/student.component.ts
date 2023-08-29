import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { Student } from './student.interface';
import { StudentService } from 'src/app/services/student.service';
import { ActivatedRoute } from '@angular/router';

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

  constructor(private studentService: StudentService, private route: ActivatedRoute, private cd: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.showMarks();
    setInterval(() => {
      this.showMarks();
    }, 5000);
  }

  showMarks(): void {
    this.studentService.getChartData().subscribe(chartData => {
      const labels: string[] = [];
      const data: number[] = [];

      for (const d of chartData) {
        const da = new Date(d.id)
        console.log(d.id);

        const parts = d.id.split(" ");
        const month = parts[1];
        const day = parseInt(parts[2]);
        const time = parts[3];
        const year = parseInt(parts[5]);

        labels.push(`${time}\n${year}-${month}-${day}`);
        data.push(d.studentCount);
      }

      const updatedChartData: ChartData<'bar'> = {
        labels: labels,
        datasets: [{ data: data, label: 'Students' }]
      };

      this.barChartData = updatedChartData;
    });
  }



}
