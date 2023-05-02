# About
팍스스쿨 앱은 학생들이 가입한 학교에서 원하는 학습 컨텐츠를 구매하고,<br> 
해당 컨텐츠에 맞는 숙제를 교사가 지정하여 제출 받는 앱 이다

학생들은 영상 시청, 퀴즈 풀이, 플래시카드 공부, E-book 보기 등 다양한 과제를 수행하며,<br> 
교사는 학생들이 제출한 숙제를 검사하여 적절한 점수를 부여할 수 있다.

이러한 기능으로 학생들은 보다 효율적으로 학습을 진행할 수 있으며,<br> 
교사는 숙제 검사와 점수 부여를 편리하게 처리할 수 있다.

# Architecture
~~~ mermaid
flowchart LR
  subgraph View
  A(Activity)
  end
  subgraph ViewModel
  B(Factory ViewModel)
  C(Api ViewModel)
  end
  
	 A(Activity)-- Event --> B(Factory ViewModel)
   B(Factory ViewModel) -. Observer .-> A(Activity)
   B(Factory ViewModel) -- Server Request --> C(Api ViewModel)
   C(Api ViewModel) -. Observer .-> B(Factory ViewModel)
   C(Api ViewModel) -- Execute --> D(Service)
   D(Service) -- Get Data --> C(Api ViewModel)
~~~

# Sequence
~~~ mermaid
sequenceDiagram
    participant Acitivity
    participant Factory ViewModel
    participant Api ViewModel
    participant Service
    
    Acitivity->>Factory ViewModel: User Actions
    Factory ViewModel->>Api ViewModel: Request Data 
    Api ViewModel->>Service: Communicate Server
    Service-->>Api ViewModel: Response Data
    Api ViewModel-->Factory ViewModel: Observer(State Flow)
    Factory ViewModel-->>Factory ViewModel: Handle Data
    Factory ViewModel-->>Acitivity: Observer(Live Data)

