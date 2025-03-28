# About
팍스스쿨 앱은 학생들이 가입한 학교에서 원하는 학습 컨텐츠를 구매하고,<br> 
해당 컨텐츠에 맞는 숙제를 교사가 지정하여 제출 받는 앱 이다

학생들은 영상 시청, 퀴즈 풀이, 플래시카드 공부, E-book 보기 등 다양한 과제를 수행하며,<br> 
교사는 학생들이 제출한 숙제를 검사하여 적절한 점수를 부여할 수 있다.

이러한 기능으로 학생들은 보다 효율적으로 학습을 진행할 수 있으며,<br> 
교사는 숙제 검사와 점수 부여를 편리하게 처리할 수 있다.
<br><br><br>
# 🏗️ Architecture

## Overview
This app is developed using the **MVVM** architectural pattern with the following key components:

### 📦 Components
- **View**: Managed by **Activity**
- **ViewModel**: Split into **FactoryViewModel** and **ApiViewModel**
- **Model**: Composed of **Data Class** and **Response Class**

## 🔍 Detailed Architecture

### Factory ViewModel's Role
- Acts as an intermediary between **View** and **ApiViewModel**
- Directly implements event handling from the View
- Transfers results via **LiveData Events**

### Data Flow
1. **View** triggers an event
2. **FactoryViewModel** processes the event
  - If API communication is required, it requests **ApiViewModel**
3. **ApiViewModel** processes data through **Service** class
4. **ApiViewModel** transfers data to **FactoryViewModel** via **StateFlow**
5. **FactoryViewModel** handles the data and notifies the **View**

## 🚀 Key Benefits
- Clear separation of View and business logic
- Enhanced code reusability and maintainability
- Real-time data reflection using **LiveData** and **StateFlow**
- Rapid and accurate information delivery to users
<br><br><br>
## 🔀 Flow
~~~ mermaid
flowchart LR
   subgraph View
   A[🖥️ Activity]
   end

   subgraph ViewModel
   B[🏭 Factory ViewModel]
   C[🌐 Api ViewModel]
   end

   subgraph Model
   D[📦 Data Class]
   E[📄 Response Class]
   end

   subgraph Service
   F[🚀 Service Class]
   end

   A -->|Event| B
   B -. LiveData .-> A
   B -->|Request| C
   C -->|StateFlow| B
   C -->|Request| F
   F -->|Response| C
   F <--> D
   F <--> E
~~~

## 🔄 Sequence 
~~~ mermaid
sequenceDiagram
   participant Activity as 🖥️ Activity
   participant FactoryVM as 🏭 Factory ViewModel
   participant ApiVM as 🌐 Api ViewModel
   participant Service as 🚀 Service

   Activity ->> FactoryVM: 👤 User Actions
   FactoryVM ->> ApiVM: 📡 Request Data 
   ApiVM ->> Service: 🌍 Server Communication
   Service -->> ApiVM: 📊 Response Data
   ApiVM ->> ApiVM: 🔄 Process Response
   ApiVM ->> FactoryVM: 📬 StateFlow Update
   FactoryVM ->> FactoryVM: 🛠️ Handle Data
   FactoryVM ->> Activity: 📢 LiveData Notification
~~~   

# UI 
### Intro 화면
<img src="https://user-images.githubusercontent.com/10841533/235601302-695a7fca-3251-4deb-b996-3c0082f5d8eb.jpg" width="220" height="500"/>

### 선생님 화면
<div><img src="https://user-images.githubusercontent.com/10841533/235601318-cb7fa4d2-1362-4bee-803c-a5393eabee95.jpg" width="220" height="500"/>
<img src="https://user-images.githubusercontent.com/10841533/235601323-2490f0e8-7749-48ee-899a-235f8072f215.jpg" width="220" height="500"/>
<img src="https://user-images.githubusercontent.com/10841533/235601334-606ecc0b-0ec9-4df9-a8d6-bb97819fe63b.jpg" width="220" height="500"/></div>

### 학생 화면
<div><img src="https://user-images.githubusercontent.com/10841533/235601340-a465f02e-20c2-4bb9-9dc6-ec6adcee9c47.jpg" width="220" height="500"/>
<img src="https://user-images.githubusercontent.com/10841533/235601346-aadde140-c70c-4e44-9428-d142eb9460b3.jpg" width="220" height="500"/>
<img src="https://user-images.githubusercontent.com/10841533/235601353-2f1cf1b0-794c-43fa-adbe-cd6551bf59be.jpg" width="220" height="500"/></div>

# TEST
    테스트 학교 : 리틀팍스 초등학교
    테스트 아이디 : Test5
    비밀번호 : 1234

# Feature
본 프로젝트는 상용화된 제품으로 모든 코드를 공개할 수 없습니다.<br>
그러나, 로그인 과정부터 메인 화면까지의 처리 과정을 간략히 보여드립니다.<br>
Paging3를 이용한 검색도 확인해 볼 수 있습니다.<br>
이 부분은 프로젝트의 전체적인 흐름을 이해하는 데 도움이 될 수 있습니다.
