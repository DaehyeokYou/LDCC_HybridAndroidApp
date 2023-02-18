# LDCC_HybridAndroidApp
롯데정보통신 인턴 프로젝트(장바구니 데이터와 GPS를 활용한 위치기반 상품 추천 서비스)

프로젝트 설명: 장바구니 데이터를 활용하여 장바구니 상품이 내 현위치(GPS) 근처에서 판매하고 있을 때 PUSH 알림을 주는 O2O 서비스

Tech: Android, Java, Cordova

개발영역: 고객의 장바구니 데이터를 활용하여 장바구니에 담아놓은 상품이 내 현위치(GPS) 근처에서 판매하고 있을 때 알림을 주는 서비스 개발

개발설명: 장바구니 상품의 오프라인 매장과 내 현위치를 GPSListener Class의 onLocationChanged이벤트 발생마다 위치의 차이를 구해서 300미터 이내이면 Push 알림을 보내는 구조
