import { Component, OnInit } from '@angular/core';
import {SchoolAverage, Student} from '../student/student.interface';
import { SchoolAverageService } from 'src/app/services/school-average.service';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';
import {StudentService} from "../../services/student.service";

@Component({
  selector: 'app-marks',
  templateUrl: './marks.component.html',
  styleUrls: ['./marks.component.scss']
})
export class MarksComponent implements OnInit {

  schoolAverages: SchoolAverage[] = [];
  students: Student[] = [];
  toppers: any;
  topperClasses: any;

  constructor(private schoolAverageService: SchoolAverageService, private authService: AuthService, private router:Router, private studentService: StudentService) { }

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

    this.studentService.getSchoolOfAdmin().subscribe((schoolNAme : string) => {
      this.studentService.getAllStudentsFromSchool(schoolNAme).subscribe((res:any) => {
        this.students = res;
      })

      this.studentService.getClassWiseTopperBySchoolName(schoolNAme).subscribe((toppers : any) => {
        this.topperClasses = Object.keys(toppers);
        this.toppers = toppers;
      })
    })


  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.removeToken();
      this.router.navigate(['/']);
    })
  }

  protected readonly Object = Object;
}
