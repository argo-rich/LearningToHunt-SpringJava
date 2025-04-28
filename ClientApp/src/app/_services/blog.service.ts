//default
import { Injectable } from '@angular/core';

// added
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Blog } from '../_models/blog';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BlogService {

private apiURL = environment.apiBaseUrl + "api/blog/";

  constructor(private http: HttpClient) { }

  getBlog(id: number): Observable<Blog> {
    return this.http.get<Blog>(this.apiURL + id);
  }
}
