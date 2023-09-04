import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgChartsModule } from 'ng2-charts';
import { HomeComponent } from './components/home/home.component';
import { StudentComponent } from './components/student/student.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { WebSocketService } from './services/web-socket.service';
import { FormsModule } from '@angular/forms';
import { MarksComponent } from './components/marks/marks.component';
import { SignupComponent } from './components/signup/signup.component';
import { AuthInterceptor } from './auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    StudentComponent,
    MarksComponent,
    SignupComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgChartsModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    WebSocketService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
