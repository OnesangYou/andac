package com.dac.gapp.andac.model

import com.dac.gapp.andac.model.firebase.NoticeInfo
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class Notice(title: String?, items: List<NoticeInfo>?) : ExpandableGroup<NoticeInfo>(title, items)