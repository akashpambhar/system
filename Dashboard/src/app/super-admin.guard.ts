import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import { Observable } from 'rxjs';
import {AuthService} from "./services/auth.service";

@Injectable({
  providedIn: 'root'
})
export class SuperAdminGuard implements CanActivate {
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const userRole = this.authService.getRole();

    if (userRole === 'ROLE_SUPERUSER') {
      return true;
    } else {
      console.log("SUPER ADMIN GUARD else Condi");
      this.router.navigate(['/']);
      return false;
    }
  }

  constructor(private authService: AuthService, private router: Router) { }

}
