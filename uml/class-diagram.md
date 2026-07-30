# EnjoyTrip 클래스 다이어그램

```mermaid
classDiagram
direction LR

class Main {
  +main(String[] args) void$
}

class ApiExplorer {
  +main(String[] args) void$
}

class EnjoyTripException {
  -serialVersionUID long$
  +EnjoyTripException()
  +EnjoyTripException(String msg)
}

class TripInfoView {
  -TripService tripService
  -JFrame frame
  -JLabel imgL
  -JLabel[] tripInfoL
  -JComboBox~String~ findC
  -JTextField wordTf
  -JButton searchBt
  -DefaultTableModel tripModel
  -JTable tripTable
  -JScrollPane tripPan
  -String[] title
  -String key
  -String[] choice
  -String word
  -TripDto curTrip
  +TripInfoView()
  -showTripInfo(int num) void
  +setMain() void
  -searchTrips() void
  +showTrips() void
}

class TripService {
  <<interface>>
  +searchAll(TripSearchDto tripSearchDto) List~TripDto~
  +search(int num) TripDto
}

class TripServiceImpl {
  -TripDao tripDao
  +TripServiceImpl()
  +searchAll(TripSearchDto tripSearchDto) List~TripDto~
  +search(int num) TripDto
}

class TripDao {
  <<interface>>
  +loadData() void
  +searchAll(TripSearchDto tripSearchDto) List~TripDto~
  +search(int num) TripDto
}

class TripDaoImpl {
  -List~TripDto~ tripInfo
  +TripDaoImpl()
  +loadData() void
  +searchAll(TripSearchDto tripSearchDto) List~TripDto~
  +search(int num) TripDto
  +print(List~TripDto~ trips) void$
}

class TripDto {
  -int num
  -String touristDestination
  -String streetAddress
  -String lotAddress
  -double lat
  -double lng
  -String info
  -String tel
  -String img
  +TripDto(int num)
  +getNum() int
  +setNum(int num) void
  +getTouristDestination() String
  +setTouristDestination(String value) void
  +getStreetAddress() String
  +setStreetAddress(String value) void
  +getLotAddress() String
  +setLotAddress(String value) void
  +getLat() double
  +setLat(double lat) void
  +getLng() double
  +setLng(double lng) void
  +getInfo() String
  +setInfo(String info) void
  +getTel() String
  +setTel(String tel) void
  +getImg() String
  +setImg(String img) void
  +toString() String
}

class TripSearchDto {
  -String touristDestination
  -String sido
  +getTouristDestination() String
  +setTouristDestination(String value) void
  +getSido() String
  +setSido(String sido) void
  +toString() String
}

class TouristDestinationSAXParser {
  -List~TripDto~ tripInfo
  -int size
  +int num
  +TouristDestinationSAXParser()
  -loadData() void
  +getTripInfo() List~TripDto~
  +setTripInfo(List~TripDto~ tripInfo) void
  +getSize() int
  +setSize(int size) void
  +getNum() int
  +setNum(int num) void
  +main(String[] args) void$
}

class TouristDestinationSAXHandler {
  -int num
  -List~TripDto~ trips
  -TripDto tripDto
  -String temp
  +TouristDestinationSAXHandler()
  +startElement(String uri, String localName, String qName, Attributes att) void
  +endElement(String uri, String localName, String qName) void
  +characters(char[] ch, int start, int length) void
  +getTrips() List~TripDto~
}

class DefaultHandler {
  <<SAX library>>
}

class Exception {
  <<Java standard library>>
}

Main ..> TripInfoView : creates
TripInfoView --> TripService : uses
TripInfoView --> TripDto : displays
TripInfoView ..> TripSearchDto : creates

TripServiceImpl ..|> TripService : implements
TripServiceImpl *-- TripDao : owns
TripService ..> TripDto : returns
TripService ..> TripSearchDto : search condition

TripDaoImpl ..|> TripDao : implements
TripDaoImpl "1" o-- "0..*" TripDto : stores
TripDaoImpl ..> TripSearchDto : filters with
TripDaoImpl ..> TouristDestinationSAXParser : creates

TouristDestinationSAXParser "1" o-- "0..*" TripDto : parsed results
TouristDestinationSAXParser ..> TouristDestinationSAXHandler : creates
TouristDestinationSAXHandler --|> DefaultHandler : extends
TouristDestinationSAXHandler "1" *-- "0..*" TripDto : creates

EnjoyTripException --|> Exception : extends
```

## 핵심 흐름

1. `Main`이 `TripInfoView`를 생성해 Swing 화면을 시작한다.
2. `TripInfoView`는 `TripService`를 통해 관광지 목록과 상세 정보를 요청한다.
3. `TripServiceImpl`은 비즈니스 요청을 `TripDao`에 위임한다.
4. `TripDaoImpl`은 `TouristDestinationSAXParser`로 XML 데이터를 읽어 `TripDto` 목록을 보관한다.
5. `TouristDestinationSAXHandler`가 XML의 각 레코드를 `TripDto`로 변환한다.

> `ApiExplorer`는 공공 API 호출 예제용 독립 실행 클래스이며, 현재 애플리케이션 실행 흐름에는 연결되어 있지 않다.
> `EnjoyTripException`도 사용자 정의 예외로 선언되어 있지만 현재 다른 클래스에서 직접 사용하지 않는다.
