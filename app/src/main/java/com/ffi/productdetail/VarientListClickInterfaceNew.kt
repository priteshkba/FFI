package com.ffi.productdetail

interface VarientListClickInterfaceNew {
    fun varientClickedPosItem(
        clickedPosition: Int,
        mRecyclerViewPosition: Int,
        mVariationListID: Int,
        mClickedVarient: Varient?
    )
}