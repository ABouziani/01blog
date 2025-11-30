import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../search/search';

interface recentPosts { id:number; title: string; creatorId: number; creatorUsername: string; media: string; }
interface activeUsers { id:number; name: string; posts: number; avatar: string; }
interface topPosts { id:number; title: string; creatorId: number; creatorUsername: string; media: string; }


@Component({
  selector: 'app-home',
  imports: [CommonModule, SearchBarComponent],
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {
  apiUrl = 'http://localhost:8080/api/home';
  constructor(private http: HttpClient, public router:Router) { }
  recentPosts: recentPosts[] = [];
  activeUsers: activeUsers[] = [];
  topPosts: topPosts[] = [];
  // recentPosts = [
  //   { title: 'My First Hackathon Experience', author: 'Alex Johnson', image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDykG5H2ZsSbB6zEtMDcblAU_ydrUaf6TwbfSro6kLKEo2rqSrwaAUH3STGgi1s38hptejMpZNIGubDlNhfQXz1exDetg4Os43ZGjn5f-p08k_F3HLrs7CtHaEwhYwgl-Glu9r7uaRrHxSn9Xa5BSzErwxVutfkNf9teSI7s9vHUI6YXXqUn64aXUs2p2ywdOV-oMaNx4-4N51RNkev8ua2ZnJQcPzEONgAvaI5R44bFCBn-CA2aAhTPHbjnOIFG7GbLd3XWSL6E3Ue' },
  //   { title: 'My First Hackathon Experience', author: 'Alex Johnson', image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDykG5H2ZsSbB6zEtMDcblAU_ydrUaf6TwbfSro6kLKEo2rqSrwaAUH3STGgi1s38hptejMpZNIGubDlNhfQXz1exDetg4Os43ZGjn5f-p08k_F3HLrs7CtHaEwhYwgl-Glu9r7uaRrHxSn9Xa5BSzErwxVutfkNf9teSI7s9vHUI6YXXqUn64aXUs2p2ywdOV-oMaNx4-4N51RNkev8ua2ZnJQcPzEONgAvaI5R44bFCBn-CA2aAhTPHbjnOIFG7GbLd3XWSL6E3Ue' },
  //   { title: 'My First Hackathon Experience', author: 'Alex Johnson', image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDykG5H2ZsSbB6zEtMDcblAU_ydrUaf6TwbfSro6kLKEo2rqSrwaAUH3STGgi1s38hptejMpZNIGubDlNhfQXz1exDetg4Os43ZGjn5f-p08k_F3HLrs7CtHaEwhYwgl-Glu9r7uaRrHxSn9Xa5BSzErwxVutfkNf9teSI7s9vHUI6YXXqUn64aXUs2p2ywdOV-oMaNx4-4N51RNkev8ua2ZnJQcPzEONgAvaI5R44bFCBn-CA2aAhTPHbjnOIFG7GbLd3XWSL6E3Ue' },
  //   { title: 'My First Hackathon Experience', author: 'Alex Johnson', image: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDykG5H2ZsSbB6zEtMDcblAU_ydrUaf6TwbfSro6kLKEo2rqSrwaAUH3STGgi1s38hptejMpZNIGubDlNhfQXz1exDetg4Os43ZGjn5f-p08k_F3HLrs7CtHaEwhYwgl-Glu9r7uaRrHxSn9Xa5BSzErwxVutfkNf9teSI7s9vHUI6YXXqUn64aXUs2p2ywdOV-oMaNx4-4N51RNkev8ua2ZnJQcPzEONgAvaI5R44bFCBn-CA2aAhTPHbjnOIFG7GbLd3XWSL6E3Ue' },
  //   // ... add others
  // ];

  // activeUsers = [
  //   { name: 'Kenji Tanaka', posts: 32, avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDPeNnSeb6aGWQBSQrcwRIG9bMKRfp5PhrT4wkzfDyP7veuhOVxx0nRoRlfcuDktOmxeNGt5H9XYJBb3BgyQDjw-OcBaL4LqLWVO8TEdc0rFBjOgh07E-5FFrtRPrJqoTJ_eAxXiUwNRnNwsIEXwrjWCXgrD6GMxg78Vc21Tu-Yz1kIhhtMv_60A6OPZKTLOYG1Or-lvEwHKDAoxbhdftJTnTIppIh-hOtThfgxiI9yixudl97P6vWgMPzO66-AZrs9ZzmFpPT4tdmT' },
  //   { name: 'Kenji Tanaka', posts: 32, avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDPeNnSeb6aGWQBSQrcwRIG9bMKRfp5PhrT4wkzfDyP7veuhOVxx0nRoRlfcuDktOmxeNGt5H9XYJBb3BgyQDjw-OcBaL4LqLWVO8TEdc0rFBjOgh07E-5FFrtRPrJqoTJ_eAxXiUwNRnNwsIEXwrjWCXgrD6GMxg78Vc21Tu-Yz1kIhhtMv_60A6OPZKTLOYG1Or-lvEwHKDAoxbhdftJTnTIppIh-hOtThfgxiI9yixudl97P6vWgMPzO66-AZrs9ZzmFpPT4tdmT' },
  //   { name: 'Kenji Tanaka', posts: 32, avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDPeNnSeb6aGWQBSQrcwRIG9bMKRfp5PhrT4wkzfDyP7veuhOVxx0nRoRlfcuDktOmxeNGt5H9XYJBb3BgyQDjw-OcBaL4LqLWVO8TEdc0rFBjOgh07E-5FFrtRPrJqoTJ_eAxXiUwNRnNwsIEXwrjWCXgrD6GMxg78Vc21Tu-Yz1kIhhtMv_60A6OPZKTLOYG1Or-lvEwHKDAoxbhdftJTnTIppIh-hOtThfgxiI9yixudl97P6vWgMPzO66-AZrs9ZzmFpPT4tdmT' },
  //   { name: 'Kenji Tanaka', posts: 32, avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDPeNnSeb6aGWQBSQrcwRIG9bMKRfp5PhrT4wkzfDyP7veuhOVxx0nRoRlfcuDktOmxeNGt5H9XYJBb3BgyQDjw-OcBaL4LqLWVO8TEdc0rFBjOgh07E-5FFrtRPrJqoTJ_eAxXiUwNRnNwsIEXwrjWCXgrD6GMxg78Vc21Tu-Yz1kIhhtMv_60A6OPZKTLOYG1Or-lvEwHKDAoxbhdftJTnTIppIh-hOtThfgxiI9yixudl97P6vWgMPzO66-AZrs9ZzmFpPT4tdmT' },
  //   { name: 'Kenji Tanaka', posts: 32, avatar: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDPeNnSeb6aGWQBSQrcwRIG9bMKRfp5PhrT4wkzfDyP7veuhOVxx0nRoRlfcuDktOmxeNGt5H9XYJBb3BgyQDjw-OcBaL4LqLWVO8TEdc0rFBjOgh07E-5FFrtRPrJqoTJ_eAxXiUwNRnNwsIEXwrjWCXgrD6GMxg78Vc21Tu-Yz1kIhhtMv_60A6OPZKTLOYG1Or-lvEwHKDAoxbhdftJTnTIppIh-hOtThfgxiI9yixudl97P6vWgMPzO66-AZrs9ZzmFpPT4tdmT' },
  //   // ...
  // ];

  // topPosts = [
  //   { title: 'Beginner\'s Guide to Machine Learning', creator: 'Sofia Nguyen', media: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBjyzelvJem47113IRUze3tXBjTsJQf1_ev3HzUr5-f_z0waiWWGg9o1C19f_brBDmO-L2jBc_kGhOpWTQ0Pfn4lSI8_R0OVKQL2B8MbAbwsX7EXwf0-JO0ML1UFL8eg5f7_84oNa6AsYHebEjeDJbX6R8zOn35Gm1byZBIWaOKDT2mxvcAPmOIengbctO4ufSfgIi8WVf9TpiH3Viin2aX07Dop1VH-_oZv0kV10k9HgX741aOxqFeKWCS22A_dw_qW37lSyyo-Icp' },
  //   { title: 'Beginner\'s Guide to Machine Learning', creator: 'Sofia Nguyen', media: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBjyzelvJem47113IRUze3tXBjTsJQf1_ev3HzUr5-f_z0waiWWGg9o1C19f_brBDmO-L2jBc_kGhOpWTQ0Pfn4lSI8_R0OVKQL2B8MbAbwsX7EXwf0-JO0ML1UFL8eg5f7_84oNa6AsYHebEjeDJbX6R8zOn35Gm1byZBIWaOKDT2mxvcAPmOIengbctO4ufSfgIi8WVf9TpiH3Viin2aX07Dop1VH-_oZv0kV10k9HgX741aOxqFeKWCS22A_dw_qW37lSyyo-Icp' },
  //   { title: 'Beginner\'s Guide to Machine Learning', creator: 'Sofia Nguyen', media: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBjyzelvJem47113IRUze3tXBjTsJQf1_ev3HzUr5-f_z0waiWWGg9o1C19f_brBDmO-L2jBc_kGhOpWTQ0Pfn4lSI8_R0OVKQL2B8MbAbwsX7EXwf0-JO0ML1UFL8eg5f7_84oNa6AsYHebEjeDJbX6R8zOn35Gm1byZBIWaOKDT2mxvcAPmOIengbctO4ufSfgIi8WVf9TpiH3Viin2aX07Dop1VH-_oZv0kV10k9HgX741aOxqFeKWCS22A_dw_qW37lSyyo-Icp' },
  //   { title: 'Beginner\'s Guide to Machine Learning', creator: 'Sofia Nguyen', media: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBjyzelvJem47113IRUze3tXBjTsJQf1_ev3HzUr5-f_z0waiWWGg9o1C19f_brBDmO-L2jBc_kGhOpWTQ0Pfn4lSI8_R0OVKQL2B8MbAbwsX7EXwf0-JO0ML1UFL8eg5f7_84oNa6AsYHebEjeDJbX6R8zOn35Gm1byZBIWaOKDT2mxvcAPmOIengbctO4ufSfgIi8WVf9TpiH3Viin2aX07Dop1VH-_oZv0kV10k9HgX741aOxqFeKWCS22A_dw_qW37lSyyo-Icp' },
  //   { title: 'Beginner\'s Guide to Machine Learning', creator: 'Sofia Nguyen', media: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBjyzelvJem47113IRUze3tXBjTsJQf1_ev3HzUr5-f_z0waiWWGg9o1C19f_brBDmO-L2jBc_kGhOpWTQ0Pfn4lSI8_R0OVKQL2B8MbAbwsX7EXwf0-JO0ML1UFL8eg5f7_84oNa6AsYHebEjeDJbX6R8zOn35Gm1byZBIWaOKDT2mxvcAPmOIengbctO4ufSfgIi8WVf9TpiH3Viin2aX07Dop1VH-_oZv0kV10k9HgX741aOxqFeKWCS22A_dw_qW37lSyyo-Icp' },
  //   // ...
  // ];

  trendingTags = [
    { label: '#ComputerScience', primary: true },
    { label: '#StudyAbroad', primary: false },
    { label: '#Internships', primary: false },
    { label: '#WebDevelopment', primary: true },
    { label: '#FinalsWeek', primary: false },
    { label: '#AI', primary: false },
    { label: '#BioTech', primary: false },
    { label: '#Hackathon', primary: true },
  ];


  ngOnInit(): void {

    this.http.get<{ recentPosts: recentPosts[], activeUsers: activeUsers[], topPosts:topPosts[] }>(this.apiUrl).subscribe({
      next: (resp) => {
        console.log(resp)
        this.recentPosts = resp.recentPosts;
        this.activeUsers = resp.activeUsers;
        this.topPosts = resp.topPosts;
      },
      error: (err) => {
        console.error('Error fetching Home data:', err);
      }
    });
  }
}