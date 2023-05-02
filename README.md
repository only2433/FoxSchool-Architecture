# About
팍스스쿨 앱은 학생들이 가입한 학교에서 원하는 학습 컨텐츠를 구매하고,<br> 
해당 컨텐츠에 맞는 숙제를 교사가 지정하여 제출 받는 앱 이다

학생들은 영상 시청, 퀴즈 풀이, 플래시카드 공부, E-book 보기 등 다양한 과제를 수행하며,<br> 
교사는 학생들이 제출한 숙제를 검사하여 적절한 점수를 부여할 수 있다.

이러한 기능으로 학생들은 보다 효율적으로 학습을 진행할 수 있으며,<br> 
교사는 숙제 검사와 점수 부여를 편리하게 처리할 수 있다.

# Architecture
이 앱은 **MVVM** 구조로 개발되어 있으며, **View**는 **Activity**가, <br>
**ViewModel**은 **FactoryViewModel**과 **ApiViewModel**로 나누어져 있습니다.<br>
**Model**은 **Data Class**와 **Response Class**로 구성되어 있습니다.

    FactoryViewModel은 View와 ApiViewModel 사이에서 중개 역할을 수행합니다.
    View에서 발생한 이벤트에 대한 처리를 직접 구현하고, 그 결과를 LiveData Event로 전달합니다.
    이때, View의 이벤트가 Api 통신을 요청하는 경우, FactoryViewModel은 ApiViewModel에 요청하고,
    ApiViewModel은 Service 클래스를 통해 데이터를 처리합니다.

    ApiViewModel은 받은 데이터를 StateFlow를 통해 FactoryViewModel에 전달합니다.
    FactoryViewModel은 이 데이터를 처리하고, 결과를 View에게 알려줍니다.
    이러한 구조를 통해 View와 비즈니스 로직을 분리하고, 코드의 재사용성과 유지보수성을 높일 수 있습니다.

    또한, LiveData와 StateFlow를 사용하여 데이터의 변경을 감지하고, 화면에 실시간으로 반영할 수 있습니다.
    이를 통해 사용자에게 빠르고 정확한 정보를 제공할 수 있습니다.
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
    Api ViewModel-->>Factory ViewModel: Observer(State Flow)
    Factory ViewModel-->>Factory ViewModel: Handle Data
    Factory ViewModel-->>Acitivity: Observer(Live Data)
~~~   
# Feature
본 프로젝트는 상용화된 제품으로 모든 코드를 공개할 수 없습니다.<br>
그러나, 로그인 과정부터 메인 화면까지의 처리 과정을 간략히 보여드립니다.<br>
이 부분은 프로젝트의 전체적인 흐름을 이해하는 데 도움이 될 수 있습니다.
