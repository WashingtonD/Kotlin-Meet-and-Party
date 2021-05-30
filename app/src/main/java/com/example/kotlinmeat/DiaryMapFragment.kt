package com.example.kotlinmeat

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.Marker


class DiaryMapFragment : Fragment() {

    private var mMarker: Marker? = null
     private var mPopupWindow: PopupWindow? = null
    private var mWidth: Int = 0
    private var mHeight: Int = 0



    private lateinit var infoWindowLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    private lateinit var infoWindowContainer: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_diary_map,null)

       if(mPopupWindow != null ) {
         mPopupWindow!!.dismiss()
       }
        val popupView = View(context)
        val popupWindow = PopupWindow(popupView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        var display = activity!!.windowManager.defaultDisplay
        var size = Point()
        display.getSize(size)
        popupView.measure(size.x,size.y)

        mWidth = popupView.measuredWidth
        mHeight = popupView.measuredHeight
        //mMarker = marker
        mPopupWindow = popupWindow

        updatePopup()

        return rootView

    }

    private fun updatePopup() {
        if(mMarker != null && mPopupWindow != null)
        {
            //if(mMap!!.getProjection)
        }
    }


}