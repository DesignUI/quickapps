package com.yoavst.quickapps.dialer

import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.content.Intent
import android.net.Uri
import com.yoavst.quickapps.DividerItemDecoration
import com.lge.qcircle.template.QCircleTemplate
import com.yoavst.quickapps.R
import com.lge.qcircle.template.TemplateTag
import com.mobsandgeeks.ake.getColor
import android.graphics.Color
import com.mobsandgeeks.ake.getPxFromDp

/**
 * Created by Yoav.
 */
public class ContactsFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recycler = RecyclerView(getActivity())
        recycler.setPadding(getPxFromDp(32), 0, getPxFromDp(32), 0)
        recycler.setLayoutManager(LinearLayoutManager(getActivity()))
        recycler.setAdapter(ContactsAdapter(getActivity()) { number -> startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)))})
        recycler.addItemDecoration(DividerItemDecoration(getActivity(), null))
        return recycler
    }
}