package com.mponline.userApp.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.ui.adapter.InstructionAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.utils.Constants
import kotlinx.android.synthetic.main.fragment_instruction.view.*


class InstructionFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_instruction, container, false)

        return mView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context!=null){
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.relative_frag?.setOnClickListener {

        }
        view?.text_proceed?.setOnClickListener {
            mSwichFragmentListener?.onSwitchFragment(Constants.CUSTOM_FOEMS_PAGE, Constants.WITH_NAV_DRAWER, null, null)
        }
        //Step
        view?.rv_steps?.layoutManager =
            LinearLayoutManager(
                activity, RecyclerView.VERTICAL, false
            )
        view?.rv_steps?.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        view?.rv_steps?.adapter = InstructionAdapter(
            activity,
            this
        )

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR,"",null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.cv_service->{
                mSwichFragmentListener?.onSwitchFragment(Constants.SUB_SERVICE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }

        }
    }
}