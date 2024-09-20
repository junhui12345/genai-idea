# AI 기반 설비 보전 예측 유지보수

## 프로젝트 설명
이 프로젝트는 Spring AI와 Llama 3.1 8b를 활용한 설비 보전 시스템(FMS) 예측 기능 데모 입니다.


설비의 고장을 예측하고, 유지보수 일정을 최적화하며, 설비 성능을 분석하는 AI 기반 솔루션을 제공합니다.


Meta Llama 모델을 사용하여 로컬 환경에서 실행 가능하여 민감한 설비 데이터를 외부 서버로 전송하지 않고 처리할 수 있습니다.

![process_overview.png](image%2Fprocess_overview.png)

## 주요 기능 설명
1. 설비 관리: 설비 정보 CRUD 기능
2. 고장 이력 관리: 설비 고장 데이터 기록 및 분석
3. 유지보수 이력 관리: 설비 유지보수 활동 추적
4. AI 기반 분석:
    - 가장 많이 유지보수된 설비 분석
    - 가장 적게 유지보수된 설비 분석
    - 최근 유지보수 분석
    - 고위험 설비 분석
5. 고장 예측: Meta Llama 3.1 8b 모델을 사용한 설비 고장 예측

## 기술 스택
- Spring Boot 3.3.3
- Spring AI 1.0.0-M2
- PostgreSQL (pgvector)
- Gradle
- Docker
- Meta Llama 3.1 8b
- Ollama (AI 모델을 쉽게 관리하고 배포할 수 있는 플랫폼) 


## Llama 3.1 8b 하드웨어 요구사항
Llama 3.1 Version Release Date: 2024-07-23

- CPU: 최소 8코어 이상의 최신 프로세서
- RAM: 최소 16GB 권장
- GPU: NVIDIA RTX 3090 (24GB) 또는 RTX 4090 (24GB)
- 스토리지: 약 20-30GB


## 프로젝트 구조

### 디렉토리 구조
```
src/main/java/genai/idea/fms/
├── controller/
├── domain/
├── repository/
└── service/
```

### 주요 컴포넌트
1. 컨트롤러 (controller/)
    - EquipmentAnalysisController: 설비 분석 API
    - EquipmentController: 설비 CRUD API
    - FailureHistoryController: 고장 이력 API
    - MaintenanceHistoryController: 유지보수 이력 API


2. 도메인 모델 (domain/)
    - Equipment: 설비 정보
    - FailureHistory: 고장 이력
    - MaintenanceHistory: 유지보수 이력


3. 리포지토리 (repository/)
    - EquipmentRepository
    - FailureHistoryRepository
    - MaintenanceHistoryRepository


4. 서비스 (service/)
    - EmbeddingService: 텍스트 및 고장 이력 임베딩
    - EquipmentAnalysisService: 설비 분석 로직 (Ollama AI 모델 사용)
    - EquipmentService: 설비 관련 비즈니스 로직
    - FailureHistoryService: 고장 이력 관련 로직
    - FailurePredictionService: 고장 예측 기능 (VectorStore 사용)
    - MaintenanceHistoryService: 유지보수 이력 관련 로직


5. AI 모델 및 임베딩
    - OllamaChatModel: 대규모 언어 모델 인터페이스
    - EmbeddingModel: 텍스트 데이터 벡터 변환


6. 벡터 저장 및 검색
    - schema.sql: vector_store 테이블 정의, pgvector 확장 활성화
    - FailurePredictionService: 벡터 저장 및 유사 고장 이력 검색


7. 설정 파일
    - application.yml: 데이터베이스, AI 모델, 벡터 저장소 설정
    - build.gradle: 프로젝트 의존성 및 Docker Compose 설정
    - docker-compose.yml: PostgreSQL 컨테이너 설정


8. 프롬프트 템플릿 (.st 파일들)
    - 다양한 분석 시나리오에 대한 프롬프트 템플릿


9. 데이터 초기화
    - data.sql: 샘플 데이터 생성


10. 메인 애플리케이션
    - FmsGenaiIdeaApplication: 애플리케이션 시작점 및 종료 로직


## AI 워크플로우 및 사용 개념

### AI 워크플로우
1. 데이터 준비: 설비, 고장, 정비 데이터 DB 저장
2. 벡터화: EmbeddingService가 텍스트 데이터 벡터 변환
3. 벡터 저장: vector_store 테이블에 벡터 저장
4. 검색: FailurePredictionService가 유사 과거 사례 검색
5. 컨텍스트 증강: EquipmentAnalysisService가 관련 데이터 결합
6. 생성: OllamaChatModel이 분석 결과 및 예측 생성
7. 응답: API를 통해 사용자에게 결과 전달
## 벡터와 임베딩

이 프로젝트에서 벡터와 임베딩은 설비 데이터의 효율적인 처리와 분석을 위한 핵심 개념입니다.

### 벡터의 역할

설비 관리 시스템에서 벡터는 설비의 상태를 수치화하여 표현합니다. 예를 들어, 설비의 상태를 다음과 같이 벡터로 나타낼 수 있습니다:

설비 상태 벡터 = (고장 이력, 수리 이력, 사용 부품 수, 설비 정지 시간, 설비 등급, 점검 항목 수)

이러한 벡터 표현을 통해 설비의 현재 상태를 간결하게 요약하고, 다른 설비와의 비교나 분석이 가능해집니다.

예시:
- 고장 이력: 5회
- 수리 이력: 3회
- 사용 부품: 8개
- 설비 정지 시간: 120시간
- 설비 등급: 1
- 점검 내용: 10개

설비 상태 벡터 = (5, 3, 8, 120, 1, 10)

### 차원의 역할

차원은 벡터에서 몇 가지 특성을 다루고 있는지를 의미합니다. 위의 예시에서 설비 상태 벡터는 6차원 벡터로, 6개의 서로 다른 값이 설비의 상태를 설명하는 데 사용됩니다.

### 차원이 많아지는 경우

설비의 상태를 더 정확히 진단하고 예측하려면 더 많은 정보를 추가할 수 있습니다. 예를 들어, **습도**, **유지보수 주기**, **부품 교체 횟수**, **운영 모드**와 같은 다양한 추가 데이터를 고려할 수 있습니다. 그러면 이 벡터는 차원이 증가하여 **10차원, 20차원**이 될 수 있습니다.

```
확장된 설비 상태 벡터 = (5, 3, 8, 120, 1, 10, 55, 30, 4, 1)
```

이렇게 차원이 높아지면 더 많은 정보를 담을 수 있지만, 차원이 높아지면 분석 및 예측 성능이 떨어질 수 있습니다.

### 임베딩

임베딩은 고차원의 데이터(예: 텍스트 설명)를 저차원의 벡터로 변환하는 과정입니다.
이 프로젝트에서는 EmbeddingService를 통해 설비 관련 텍스트 데이터를 벡터로 변환합니다.

### 벡터 저장소 (Vector Store)

PostgreSQL의 pgvector 확장을 사용하여 벡터 데이터를 효율적으로 저장하고 검색합니다. 이를 통해 다음과 같은 이점을 얻을 수 있습니다:

1. 비정형 데이터 처리: 텍스트 형태의 설비 정보를 벡터로 저장하고 검색할 수 있습니다.
2. 유사도 검색: 벡터 간 유사도를 기반으로 유사한 설비 상태나 고장 패턴을 빠르게 찾을 수 있습니다.

### 벡터와 차원의 활용
설비 보전 시스템에서 벡터와 차원을 활용하면, 설비의 과거 데이터를 바탕으로 미래의 고장 가능성을 예측하는 데 사용할 수 있습니다.
각 설비의 상태 벡터를 입력으로 받아들이고, 고장 패턴을 학습하여 다음과 같은 예측을 할 수 있습니다:

- 고장 발생 확률 예측
- 유지보수 시점 추천
- 부품 교체 주기 최적화
- 설비 벡터의 유사도 계산을 통해 비슷한 설비의 고장 예측

### 유사도 계산
벡터를 활용한 중요한 개념 중 하나는 **유사도 계산**입니다. 두 설비의 상태 벡터가 비슷할수록, 그 설비들은 비슷한 상태에 있다고 볼 수 있습니다.
이를 통해 비슷한 조건에서 다른 설비의 고장 이력을 참고하여 해당 설비의 고장 가능성을 미리 예측할 수 있습니다.

예를 들어, 설비 A와 설비 B의 상태 벡터가 다음과 같다면:

```
설비 A 상태 벡터 = (75, 0.3, 5000, 2, 120)
설비 B 상태 벡터 = (70, 0.25, 4800, 1, 115)
```

이 두 벡터 간의 KNN 알고리즘 (유클리디안 거리)를 사용하여 설비 A와 B가 유사한 상태에 있는지 판단할 수 있습니다.
유사도가 높으면, 설비 A가 B와 비슷한 시간에 고장이 날 가능성이 있다는 결론을 내릴 수 있습니다.

이러한 벡터 기반 접근 방식은 FailurePredictionService에서 활용되어 고장 예측 및 분석 기능을 제공합니다.

```
참고)
유클리디안 거리(Euclidean Distance)
두 점 사이의 직선 거리를 측정하는 방법
d = √((x₂ - x₁)² + (y₂ - y₁)²)
```

### AI 개념 적용
1. 텍스트 생성 (Text Generation):
    - 적용: OllamaChatModel
    - 위치: EquipmentAnalysisService
    - 주요 메서드: findEquipmentWithMostMaintenanceHistory(), findEquipmentWithLeastMaintenanceHistory(), findMostRecentMaintenanceHistory(), findEquipmentWithHighFailureProbability()
    - 목적: 설비 분석 결과를 자연어로 설명하는 응답 생성


2. 임베딩 (Embeddings):
    - 적용: EmbeddingService
    - 위치: EmbeddingService, FailurePredictionService
    - 주요 메서드: embedText(), embedFailureHistory()
    - 목적: 설비, 고장, 정비 데이터를 벡터로 변환하여 유사성 검색에 활용


3. 프롬프트 엔지니어링 (Prompt Engineering):
    - 적용: SystemPromptTemplate
    - 위치: EquipmentAnalysisService, 리소스 폴더의 .st 파일들
    - 파일: high-failure-probability.st, least-maintenance.st, most-maintenance.st, recent-maintenance.st
    - 목적: 분석 상황에 맞는 동적 프롬프트 생성으로 LLM 응답의 품질 향상


4. 유사성 검색 (Similarity Search):
    - 적용: VectorStore
    - 위치: FailurePredictionService
    - 주요 메서드: findSimilarFailures()
    - 목적: 유사한 고장 패턴이나 정비 이력을 찾아 예측 및 분석에 활용


5. 검색 증강 생성 (Retrieval Augmented Generation, RAG):
    - 적용: FailurePredictionService와 EquipmentAnalysisService 조합
    - 프로세스:
        1. findSimilarFailures()로 유사 고장 사례 검색
        2. 검색된 사례와 추가 컨텍스트를 LLM 프롬프트에 포함
        3. OllamaChatModel을 통해 증강된 정보를 바탕으로 분석 결과 생성
    - 목적: 외부 데이터(과거 고장 이력)를 활용하여 더 정확하고 관련성 높은 AI 응답 생성


6. 워크플로우

![embedding.png](image%2Fembedding.png)


## 설치 및 실행 방법 (Windows 기준)
0. Ollama 설치:
    - [Ollama 공식 웹사이트](https://ollama.ai/download)에서 Windows용 Ollama를 다운로드하고 설치합니다.
    - 설치 후 Ollama를 실행하고, 터미널에서 다음 명령어를 실행하여 필요한 모델을 다운로드합니다:
      ```
      ollama run llama3.1
      ```

1. Docker Desktop 설치:
    - [Docker Desktop 공식 웹사이트](https://www.docker.com/products/docker-desktop/)에서 Windows용 Docker Desktop을 다운로드하고 설치합니다.
    - 설치 완료 후 Docker Desktop을 실행합니다.

2. 프로젝트 클론:
    - Git이 설치되어 있지 않다면 [Git 공식 웹사이트](https://git-scm.com/download/win)에서 다운로드하고 설치합니다.
    - Windows 명령 프롬프트(CMD) 또는 PowerShell을 열고 다음 명령어를 실행합니다:
      ```
      git clone https://junhui123@bitbucket.org/junhui123/genai_idea.git
      cd fms-genai-idea
      ```

3. 프로젝트 실행:
    - Spring Boot 애플리케이션 실행 시 자동으로 필요한 Docker 컨테이너를 시작하고, 데이터베이스를 초기화 합니다.

4. 애플리케이션 접속:
    - 브라우저를 열고 `http://localhost:8080`에 접속하여 애플리케이션을 사용할 수 있습니다.
    - API 엔드포인트
    - `/api/equipment`: 설비 관리
    - `/api/failure-history`: 고장 이력 관리
    - `/api/maintenance-history`: 유지보수 이력 관리
    - `/api/analysis`: AI 기반 분석 결과 제공

추가 안내사항:
- 이 애플리케이션은 데모 목적으로 설계되어 있어, 서버가 재시작될 때마다 데이터베이스 테이블이 새로 생성됩니다. 이는 매번 새로운 상태에서 애플리케이션을 테스트할 수 있도록 하기 위함입니다.

문제 해결:
- Docker 관련 오류가 발생한다면 Docker Desktop이 실행 중인지 확인하세요.
- Ollama 관련 오류가 발생한다면 Ollama 서비스가 실행 중인지 확인하고, 필요한 모델이 올바르게 다운로드되었는지 확인하세요.

## 실행 방법 및 결과

가장 최근 유지보수 설비
- GET http://localhost:8080/api/analysis/most-maintained-equipment

```
컨베이어 벨트 A (ID: 1)의 최근 유지보수는 2024-07-23에 수행되었습니다.

**유지보수의 분석**

*   **시간적 위치**: 2024년 7월 23일은 여름철의 중간 즈음입니다. 이 시기는 일반적으로 공장 운영에서 생산량이 증가하고, 제품의 품질 관리가 중요해지는 시기입니다.
*   **유지보수 유형**: '사후정비'는 장치의 유지보수를 의미하며, 컨베이어 속도 제어 장치 교체 및 조정이라는 상세한 설명을 포함합니다. 이는 장치의 성능 향상과 안정성을 보장하기 위한 작업입니다.
*   **성능 영향**: 이 유지보수는 장치의 성능 향상과 안정성을 보장하는 데 도움이 될 것입니다. 그러나, 컨베이어 속도 제어 장치 교체 및 조정은 일반적으로 장치의 운영 시간을 중단시키고, 생산 라인의 중단으로 이어질 수 있습니다.

**추가 작업 및 모니터링**

*   **장치 성능 모니터링**: 유지보수 후 장치의 성능이 원래대로 회복되었는지 확인하기 위해 주기적인 모니터링을 수행해야 합니다.
*   **사후정비 결과 분석**: 유지보수의 상세한 결과를 분석하여, 향후 유지보수를 계획하고, 장치의 성능 향상에 도움이 될 수 있는 개선점을 파악할 수 있습니다.
*   **장치 운영 기록 관리**: 장치의 운영 기록을 관리하여, 유지보수 기록과 함께 저장해야 합니다. 이로 인해, 장치의 성능이 시간에 따라 어떻게 변하는지 파악하고, 향후 유지보수를 계획할 수 있습니다.

**추가 조치**

*   **장치의 안전성 확보**: 유지보수 후 장치의 안전성을 확보하기 위해, 주기적인 안전 점검을 수행해야 합니다.
*   **사용자 교육**: 사용자가 장치의 새로운 기능과 운영 방법에 대해 교육받아야 합니다. 이로 인해, 사용자가 장치의 성능 향상에 도움이 될 수 있습니다.

이 유지보수는 장치의 성능 향상과 안정성을 보장하기 위한 중요한 작업입니다. 그러나, 추가적인 작업과 모니터링을 통해, 장치의 성능 향상과 안전성 확보를 보장할 수 있습니다.
```


가장 적은 유지보수가 이루어지고 있는 장비 실행 결과
- GET http://localhost:8080/api/analysis/least-maintained-equipment


```
기계 '크레인 F' (ID: 6)가 가장 적은 유지보수 기록을 가지고 있는 것으로 보입니다. 이 기계의 유지보수 기록이 다른 기계보다 적게 남아있는 이유를 분석해 보겠습니다.

1. **나이**: 크레인 F의 나이는 불명확하지만, 다른 기계와 비교했을 때 상대적으로 젊은 기계일 수 있습니다. 새로운 기기는 일반적으로 오래된 기계에 비해 더 많은 유지보수를 필요로 하지 않습니다.
2. **유형**: 크레인 F의 유형은 알려져 있지 않지만, 이 기계가 다른 기계와 비교했을 때 상대적으로 단순하고 안정적인 설계일 수 있습니다. 단순한 설계는 일반적으로 오류를 줄이고 유지보수를 용이하게 만듭니다.
3. **사용**: 크레인 F의 사용 빈도나 시간은 알려져 있지 않지만, 이 기계가 다른 기계와 비교했을 때 상대적으로 적게 사용되었을 수 있습니다. 적게 사용된 기기는 일반적으로 더 오래 유지될 수 있으며, 유지보수를 필요로 하지 않을 가능성이 높습니다.
4. **품질**: 크레인 F의 품질은 알려져 있지 않지만, 이 기계가 다른 기계와 비교했을 때 상대적으로 높은 품질을 가지고 있을 수 있습니다. 높은 품질의 기기는 일반적으로 오류를 줄이고 유지보수를 용이하게 만듭니다.
5. **유지보수 기록**: 크레인 F가 가장 적은 유지보수 기록을 가지고 있는 것은, 이 기계가 다른 기계와 비교했을 때 상대적으로 안정적이고 신뢰성이 높은 것으로 보입니다.

결론적으로, 크레인 F의 유지보수 기록이 다른 기계보다 적게 남아있는 이유는 여러 가지 요인이 있을 수 있습니다. 하지만, 가장 가능성 있는 이유는 이 기계가 상대적으로 젊은 나이에, 단순한 설계에, 적게 사용되었을 수 있으며, 높은 품질을 가지고 있었기 때문입니다.
이 유지보수는 장치의 성능 향상과 안정성을 보장하기 위한 중요한 작업입니다. 그러나, 추가적인 작업과 모니터링을 통해, 장치의 성능 향상과 안전성 확보를 보장할 수 있습니다.
```


고장 확률이 높은 설비
- GET http://localhost:8080/api/analysis/highest-failure-probability

```json
{
  "equipmentId": 2,
  "name": "산업용 펌프 B",
  "type": "펌프",
  "installationDate": "2019-07-22",
  "manufacturer": "PumpMaster",
  "model": "PM-500",
  "status": "정상",
  "failureHistories": [
    {
      "failureId": 3,
      "failureDate": "2021-09-05",
      "failureType": "기계적",
      "description": "펌프 임펠러 손상",
      "downtime": 8.0,
      "repairCost": 3000.00,
      "maintenanceHistories": [
        {
          "maintenanceId": 3,
          "maintenanceDate": "2021-09-06",
          "maintenanceType": "사후정비",
          "description": "펌프 임펠러 교체 및 밸런싱",
          "cost": 3500.00,
          "technician": "박엔지니어"
        }
      ]
    },
    ...
  ],
  "maintenanceHistories": [
    {
      "maintenanceId": 3,
      "maintenanceDate": "2021-09-06",
      "maintenanceType": "사후정비",
      "description": "펌프 임펠러 교체 및 밸런싱",
      "cost": 3500.00,
      "technician": "박엔지니어"
    },
    ...
  ]
}
```


## 참고 자료
- [Spring AI 문서](https://docs.spring.io/spring-ai/reference/index.html)
- [Ollama 공식 웹사이트](https://ollama.ai/)

## 라이센스 정보
- Ollama https://github.com/ollama/ollama/blob/main/LICENSE
- Llama 3  https://www.llama.com/llama3/license/
