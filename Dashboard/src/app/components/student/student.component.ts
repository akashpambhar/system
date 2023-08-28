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
    this.route.paramMap.subscribe(params => {
      this.studentId = String(params.get('id'));
      this.showMarks(this.studentId);
    });
  }

  showMarks(studentId: string): void {
    this.studentService.getStudentById(studentId).subscribe(student => {
      this.selectedStudent = student;
      const labels: string[] = [];
      const data: number[] = [];

      for (const mark of this.selectedStudent.marks) {
        labels.push(mark.subjectName);
        data.push(mark.marks);
      }

      const updatedChartData: ChartData<'bar'> = {
        labels: labels,
        datasets: [{ data: data, label: this.selectedStudent.studentName }]
      };

      this.barChartData = updatedChartData;
    });
  }



}
