# 숏폼 스트룹 실험 (Short-form Stroop Experiment)

숏폼 영상 시청이 주의력에 미치는 영향을 측정하기 위한 스트룹 과제 웹 애플리케이션

## 실험 개요

- **목적**: 숏폼 영상 시청 전후 주의력 변화 측정
- **방법**: 스트룹 과제 (단어의 색상 판별)
- **측정 시점**: 기저선 → 영상 시청 → 0분 후 → 3분 후

## 기술 스택

- **Backend**: Spring Boot 4.0, Java 21
- **Frontend**: Vanilla HTML/CSS/JavaScript
- **Data**: CSV 파일 저장

## 실행 방법

```bash
./gradlew bootRun
```

서버 실행 후 `http://localhost:8080` 접속

## 프로젝트 구조

```
src/main/
├── java/.../stroop_experiment/
│   ├── StroopExperimentApplication.java  # 메인
│   ├── DataController.java               # REST API
│   ├── DataService.java                  # 데이터 저장/조회
│   ├── ExperimentData.java               # 실험 데이터 DTO
│   └── Trial.java                        # 개별 시행 DTO
└── resources/static/
    ├── index.html              # 홈
    ├── stroop_experiment.html  # 실험 페이지
    └── admin.html              # 관리자 페이지
```

## API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/save` | 실험 데이터 저장 |
| GET | `/api/participants` | 참가자 목록 조회 |
| GET | `/api/participant/{id}` | 개별 데이터 조회 |
| DELETE | `/api/participant/{id}` | 데이터 삭제 |
| GET | `/api/master-csv` | 전체 데이터 병합 다운로드 |
