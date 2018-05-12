package com.dac.gapp.andac

// 카드뷰에서 사용할 클래스 적어줌.
class CardItem// 컨서트럭트해줌
(// 게터세터해줌
        var title: String?, var contents: String?) {


    //toString 해줌
    override fun toString(): String {
        return "CardItem{" +
                "title='" + title + '\''.toString() +
                ", contents='" + contents + '\''.toString() +
                '}'.toString()
    }
}
