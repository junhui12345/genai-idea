CREATE EXTENSION IF NOT EXISTS vector;

DROP TABLE IF EXISTS vector_store;
DROP TABLE IF EXISTS Maintenance_History;
DROP TABLE IF EXISTS Failure_History;
DROP TABLE IF EXISTS Equipment;

CREATE TABLE vector_store (
    id UUID PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding vector(4096)
);

-- 설비 정보 테이블 생성
CREATE TABLE Equipment (
    equipment_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    installation_date DATE,
    manufacturer VARCHAR(100),
    model VARCHAR(100),
    status VARCHAR(20)
);

-- Equipment 테이블 및 컬럼에 대한 코멘트
COMMENT ON TABLE Equipment IS '설비 정보를 저장하는 테이블';
COMMENT ON COLUMN Equipment.equipment_id IS '설비 고유 식별자';
COMMENT ON COLUMN Equipment.name IS '설비 이름';
COMMENT ON COLUMN Equipment.type IS '설비 유형 (예: 펌프, 모터, 컨베이어 등)';
COMMENT ON COLUMN Equipment.installation_date IS '설비 설치 일자';
COMMENT ON COLUMN Equipment.manufacturer IS '제조사 이름';
COMMENT ON COLUMN Equipment.model IS '설비 모델명';
COMMENT ON COLUMN Equipment.status IS '현재 설비 상태 (예: 정상, 점검 중, 고장 등)';

-- 고장 이력 테이블 생성
CREATE TABLE Failure_History (
    failure_id INT PRIMARY KEY,
    equipment_id INT,
    failure_date DATE NOT NULL,
    failure_type VARCHAR(50),
    description TEXT,
    downtime FLOAT,
    repair_cost DECIMAL(10, 2),
    FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id)
);

-- Failure_History 테이블 및 컬럼에 대한 코멘트
COMMENT ON TABLE Failure_History IS '설비 고장 이력을 저장하는 테이블';
COMMENT ON COLUMN Failure_History.failure_id IS '고장 이력 고유 식별자';
COMMENT ON COLUMN Failure_History.equipment_id IS '고장이 발생한 설비 ID (외래키)';
COMMENT ON COLUMN Failure_History.failure_date IS '고장 발생 일자';
COMMENT ON COLUMN Failure_History.failure_type IS '고장 유형 (예: 기계적, 전기적, 소프트웨어 등)';
COMMENT ON COLUMN Failure_History.description IS '고장에 대한 상세 설명';
COMMENT ON COLUMN Failure_History.downtime IS '고장으로 인한 가동 중지 시간 (시간 단위)';
COMMENT ON COLUMN Failure_History.repair_cost IS '수리 비용';

-- 정비 이력 테이블 생성
CREATE TABLE Maintenance_History (
    maintenance_id INT PRIMARY KEY,
    equipment_id INT,
    failure_id INT NULL, -- NULL 허용, 모든 정비가 고장과 관련있는 것은 아니므로 (정기 점검, 예방 정비 등)
    maintenance_date DATE NOT NULL,
    maintenance_type VARCHAR(50),
    description TEXT,
    cost DECIMAL(10, 2),
    technician VARCHAR(100),
    FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id),
    FOREIGN KEY (failure_id) REFERENCES Failure_History(failure_id)
);

-- Maintenance_History 테이블 및 컬럼에 대한 코멘트
COMMENT ON TABLE Maintenance_History IS '설비 정비 이력을 저장하는 테이블';
COMMENT ON COLUMN Maintenance_History.maintenance_id IS '정비 이력 고유 식별자';
COMMENT ON COLUMN Maintenance_History.equipment_id IS '정비 대상 설비 ID (외래키)';
COMMENT ON COLUMN Maintenance_History.failure_id IS '관련 고장 ID (외래키, NULL 허용)';
COMMENT ON COLUMN Maintenance_History.maintenance_date IS '정비 수행 일자';
COMMENT ON COLUMN Maintenance_History.maintenance_type IS '정비 유형 (예: 예방정비, 사후정비, 상태기반정비 등)';
COMMENT ON COLUMN Maintenance_History.description IS '정비 작업에 대한 상세 설명';
COMMENT ON COLUMN Maintenance_History.cost IS '정비 비용';
COMMENT ON COLUMN Maintenance_History.technician IS '정비 수행 기술자 이름';