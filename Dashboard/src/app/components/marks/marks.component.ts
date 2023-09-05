import { Component, OnInit } from '@angular/core';
import { SchoolAverage } from '../student/student.interface';
import { SchoolAverageService } from 'src/app/services/school-average.service';

@Component({
  selector: 'app-marks',
  templateUrl: './marks.component.html',
  styleUrls: ['./marks.component.scss']
})
export class MarksComponent implements OnInit {

  schoolAverages: SchoolAverage[] = [];

  constructor(private schoolAverageService: SchoolAverageService) { }

  ngOnInit() {
    this.schoolAverageService.getSchoolAverages()
      .subscribe(
        (data) => {
          this.schoolAverages = data;
        },
        (error) => {
          console.error('Error fetching school averages:', error);
        }
      );
  }

}
