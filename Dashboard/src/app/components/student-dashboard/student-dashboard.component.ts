import { Component, OnInit } from '@angular/core';
import { StudentService } from 'src/app/services/student.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';


@Component({
  selector: 'app-student-dashboard',
  templateUrl: './student-dashboard.component.html',
  styleUrls: ['./student-dashboard.component.scss']
})
export class StudentDashboardComponent implements OnInit {

  student : any;
  topper : any;
  averageMarks : any;

  constructor(private studentService: StudentService, private route: ActivatedRoute, private authService : AuthService, private router : Router) { }

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get("studentId");
    this.studentService.getStudentDashboard(param!).subscribe((res) : any => {
      
      this.student = res[0];
      this.topper = res[1];
    })
  }

  calculateAverageMarks(): number {
    const totalMarks = this.student.marks.reduce((acc : any, mark:any) => acc + mark.marks, 0);
    return totalMarks / this.student.marks.length;
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.removeToken();
      this.router.navigate(['/']);
    })
  }
}
