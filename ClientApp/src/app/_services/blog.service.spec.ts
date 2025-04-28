import { TestBed } from '@angular/core/testing';
import { BlogService } from './blog.service';

// added imports
import { provideHttpClient } from "@angular/common/http";

describe('BlogService', () => {
  let service: BlogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient()
      ]
    });
    service = TestBed.inject(BlogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
