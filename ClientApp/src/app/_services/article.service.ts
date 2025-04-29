//default
import { Injectable } from '@angular/core';

// added
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Article } from '../_models/article';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  private apiURL = environment.apiBaseUrl + "api/article/get/";

  constructor(private http: HttpClient) { }

  getArticle(id: number): Observable<Article> {
    return this.http.get<Article>(this.apiURL + id);
  }

  updateArticle(id: number, updatedArticle: Article): Observable<void> {
    updatedArticle.articleId = id;
    return this.http.put<void>(this.apiURL + id, updatedArticle);
  }
}
