//default
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BlogComponent } from './blog.component';

//added
import { provideHttpClient } from "@angular/common/http";
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { BlogService } from '../_services/blog.service';
import { of } from 'rxjs';
import { Observable } from 'rxjs';
import { Blog } from '../_models/blog';

let blog1: Blog = {
  blogId: 1,
  title: "Test Blog Title",
  subtitle: "First Blog Subtitle",
  content: "Test blog content.",
  authorId: 1,
  createDate: new Date("2025-01-06T13:30:49"),
  modifyDate: new Date("2025-01-06T13:30:49")
};

let blog2: Blog = {
  blogId: 2,
  title: "Test Blog Title2",
  subtitle: "First Blog Subtitle2",
  content: "Test blog content2.",
  authorId: 2,
  createDate: new Date("2025-02-06T13:30:49"),
  modifyDate: new Date("2025-02-06T13:30:49")
};

// Mock BlogService
class MockBlogService {
  getBlog(id: number): Observable<Blog> {    
    return of((id === 1) ? blog1 : blog2);
  }
}

// Mock ActivatedRoute
const mockActivatedRoute = {
  snapshot: { 
    paramMap: {
      get(id: string): string {        
        return '1';
      }
    }
  }
};

// set up for the BlogComponent
describe('BlogComponent', () => {
  let component: BlogComponent;
  let fixture: ComponentFixture<BlogComponent>;
  let blogService: BlogService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlogComponent],
      providers: [
        { provide: BlogService, useClass: MockBlogService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        provideHttpClient()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlogComponent);
    component = fixture.componentInstance;
    blogService = TestBed.inject(BlogService);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('init should retreive blog1 from BlogService', () => {
    expect(component.blog).toBe(blog1);
  });

  it('should retreive blog2 from BlogService', () => {
    component.getBlog(2);
    expect(component.blog).toBe(blog2);
  });

});
