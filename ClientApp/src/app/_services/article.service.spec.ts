import { TestBed } from '@angular/core/testing';
import { ArticleService } from './article.service';

// added imports
import { provideHttpClient } from "@angular/common/http";

describe('ArticleService', () => {
  let service: ArticleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient()
      ]
    });
    service = TestBed.inject(ArticleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
