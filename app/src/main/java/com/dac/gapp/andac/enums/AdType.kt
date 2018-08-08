package com.dac.gapp.andac.enums

/**
 * 광고는 새로운 컨텐츠 생성이 아닌, 내 병원 / 내 이벤트 2가지를 홍보하기 위한 수단으로 이용되는것으로 결정함.
 * -> 그럼 내 이벤트가 없는 경우에는 Ad.MAIN_POPUP, Ad.MAIN_BANNER 광고 신청 불가능??
 */
enum class AdType {
    NONE, // 해당 없음
    HOSPITAL, // 병원 홍보
    EVENT, // 이벤트 홍보
}