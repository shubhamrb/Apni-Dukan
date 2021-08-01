package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.CustomFieldObj
import com.mponline.userApp.util.CommonUtils
import kotlinx.android.synthetic.main.fragment_custom_form.view.*
import kotlinx.android.synthetic.main.item_btn.view.*
import kotlinx.android.synthetic.main.item_chkbox_custfield.view.*
import kotlinx.android.synthetic.main.item_edittext.view.*
import kotlinx.android.synthetic.main.item_fileupload.view.*
import kotlinx.android.synthetic.main.item_spinner.view.*


class CustomFileAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    public var mList: ArrayList<CustomFieldObj>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEWTYPE_TEXT = 1;
    var VIEWTYPE_MULT_TEXT = 2;
    var VIEWTYPE_DROPDOWN = 3;
    var VIEWTYPE_CHECKBOX = 4;
    var VIEWTYPE_RADIOBTN = 5;
    var VIEWTYPE_BUTTON = 6;
    var VIEWTYPE_FILEPICKER = 7;
    var VIEWTYPE_LOCATION = 8;
    var isAllowedForListener = false

    init {
        VIEWTYPE_TEXT = 1;
        VIEWTYPE_MULT_TEXT = 2;
        VIEWTYPE_DROPDOWN = 3;
        VIEWTYPE_CHECKBOX = 4;
        VIEWTYPE_RADIOBTN = 5;
        VIEWTYPE_BUTTON = 6;
        VIEWTYPE_FILEPICKER = 7;
        VIEWTYPE_LOCATION = 8;
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEWTYPE_TEXT) {
            return TextViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_edittext,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEWTYPE_MULT_TEXT) {
            return TextMultViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_edittext,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEWTYPE_DROPDOWN) {
            return DropdownViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_spinner,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEWTYPE_CHECKBOX) {
            return CheckboxViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_chkbox_custfield,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEWTYPE_RADIOBTN) {
            return RadiobtnViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_chkbox_custfield,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEWTYPE_BUTTON) {
            return ButtonViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_btn,
                    parent,
                    false
                )
            )
        } else if (viewType == VIEWTYPE_FILEPICKER) {
            return FilePickerViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_fileupload,
                    parent,
                    false
                )
            )
        } else {
            return TextViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_fileupload,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.setIsRecyclable(false);
        if (holder is FilePickerViewHolder) {
            holder.itemView.ll_file_upload_container.visibility =
                if (mList?.get(position)?.isVisible) View.VISIBLE else View.GONE
            if (!mList?.get(position)?.ansValue?.isNullOrEmpty()!!) {
                holder.itemView.ll_selected_file.visibility = View.VISIBLE
                holder.itemView.text_filename.text = mList?.get(position)?.ansValue
            } else {
                holder.itemView.ll_selected_file.visibility = View.GONE
            }

            holder.itemView.text_file_label.text = mList?.get(position)?.hintName
            holder.itemView.text_file_upload_label.text = Html.fromHtml(mList?.get(position)?.label)
            holder.itemView.text_file_upload.setOnClickListener {
                listener.onClick(position, holder.itemView.text_file_upload, mList?.get(position))
            }
            holder.itemView.image_file_close.setOnClickListener {
                listener.onClick(position, holder.itemView.image_file_close, mList?.get(position))
            }
        } else if (holder is TextViewHolder) {
            holder.itemView.ll_edittext_container.visibility =
                if (mList?.get(position)?.isVisible) View.VISIBLE else View.GONE
            CommonUtils.printLog("ANS_TXT", "${mList?.get(position)?.ansValue}")
            holder.itemView.text_edt_upload_label.text = Html.fromHtml(mList?.get(position)?.label)
            if (mList?.get(position)?.fieldType?.equals("text")!!) {
                holder.itemView.edt_custom_field.setHint(Html.fromHtml(mList?.get(position)?.hintName))
                holder.itemView.edt_custom_field.setText(mList?.get(position)?.ansValue)
                holder.itemView.edt_custom_field.visibility = View.VISIBLE
                holder.itemView.edt_custom_field_number.visibility = View.GONE
                holder.itemView.edt_custom_field_mult.visibility = View.GONE
            } else if (mList?.get(position)?.fieldType?.equals("number")!!) {
                holder.itemView.edt_custom_field.visibility = View.GONE
                holder.itemView.edt_custom_field_number.setHint(Html.fromHtml(mList?.get(position)?.hintName))
                holder.itemView.edt_custom_field_number.setText(mList?.get(position)?.ansValue)
                holder.itemView.edt_custom_field_number.visibility = View.VISIBLE
                holder.itemView.edt_custom_field_mult.visibility = View.GONE
//                holder.itemView.edt_custom_field_mult.inputType = InputType.TYPE_CLASS_NUMBER
            } else {
                holder.itemView.edt_custom_field.visibility = View.GONE
                holder.itemView.edt_custom_field_mult.visibility = View.VISIBLE
                holder.itemView.edt_custom_field_number.visibility = View.GONE
                holder.itemView.edt_custom_field_mult.setHint(Html.fromHtml(mList?.get(position)?.hintName))
            }
            holder.bind(context, mList?.get(position), position, listener)
        }  else if (holder is TextMultViewHolder) {
            holder.itemView.ll_edittext_container.visibility =
                if (mList?.get(position)?.isVisible) View.VISIBLE else View.GONE
            holder.itemView.text_edt_upload_label.text = Html.fromHtml(mList?.get(position)?.label)
            CommonUtils.printLog("ANS_TXT", "${mList?.get(position)?.ansValue}")
            if (mList?.get(position)?.fieldType?.equals("text")!!) {
                holder.itemView.edt_custom_field.setHint(Html.fromHtml(mList?.get(position)?.name))
                holder.itemView.edt_custom_field.setText(mList?.get(position)?.ansValue)
                holder.itemView.edt_custom_field.visibility = View.VISIBLE
                holder.itemView.edt_custom_field_number.visibility = View.GONE
                holder.itemView.edt_custom_field_mult.visibility = View.GONE
            } else if (mList?.get(position)?.fieldType?.equals("number")!!) {
                holder.itemView.edt_custom_field.visibility = View.GONE
                holder.itemView.edt_custom_field.setHint(Html.fromHtml(mList?.get(position)?.name))
                holder.itemView.edt_custom_field.setText(mList?.get(position)?.ansValue)
                holder.itemView.edt_custom_field_number.visibility = View.VISIBLE
                holder.itemView.edt_custom_field_mult.visibility = View.GONE
//                holder.itemView.edt_custom_field_mult.inputType = InputType.TYPE_CLASS_NUMBER
            } else {
                holder.itemView.edt_custom_field.visibility = View.GONE
                holder.itemView.edt_custom_field_mult.visibility = View.VISIBLE
                holder.itemView.edt_custom_field_number.visibility = View.GONE
                holder.itemView.edt_custom_field_mult.setHint(Html.fromHtml(mList?.get(position)?.name))
            }
            holder.bind(context, mList?.get(position), position, listener)
        } else if (holder is DropdownViewHolder) {
            holder.itemView.ll_spn_container.visibility =
                if (mList?.get(position)?.isVisible) View.VISIBLE else View.GONE
            CommonUtils.printLog("ANS_DROPDOWN", "${mList?.get(position)?.ansValue}")
            if (mList?.get(position)?.value != null && !mList?.get(position)?.value?.isNullOrEmpty()!!) {
//                var dropdownItemList = mList?.get(position)?.value?.split(",")
                holder.itemView.text_spn_title.text = Html.fromHtml(mList?.get(position)?.label)
                if (!isAllowedForListener) {
                    holder.bind(context, mList?.get(position), position, listener)
                }
            }
        } else if (holder is CheckboxViewHolder) {
            holder.itemView.ll_radio_checkbox_container.visibility =
                if (mList?.get(position)?.isVisible) View.VISIBLE else View.GONE
            var chkboxItemList = mList?.get(position)?.value
            holder.itemView.text_chkbox_title.setText(Html.fromHtml(mList?.get(position)?.label))
            holder.itemView.rv_chkbox?.layoutManager =
                LinearLayoutManager(
                    context, RecyclerView.VERTICAL, false
                )
            holder.itemView.rv_chkbox?.adapter = ChkboxRadiobtnAdapter(
                context,
                listener, chkboxItemList!!,
                "chkbox",
                mList?.get(position)?.ansValue!!,
                position
            )
        } else if (holder is RadiobtnViewHolder) {
            holder.itemView.ll_radio_checkbox_container.visibility =
                if (mList?.get(position)?.isVisible) View.VISIBLE else View.GONE
            holder.itemView.text_chkbox_title.setText(Html.fromHtml(mList?.get(position)?.label))
            var radiobtnItemList = mList?.get(position)?.value
            holder.itemView.rv_chkbox?.layoutManager =
                LinearLayoutManager(
                    context, RecyclerView.VERTICAL, false
                )
            holder.itemView.rv_chkbox?.adapter = ChkboxRadiobtnAdapter(
                context,
                listener, radiobtnItemList!!,
                "radiobtn",
                mList?.get(position)?.ansValue!!,
                position
            )
        } else if (holder is ButtonViewHolder) {
            holder.itemView.ll_btn_container.visibility =
                if (mList?.get(position)?.isVisible) View.VISIBLE else View.GONE
            holder.itemView.text_btn_label.text = Html.fromHtml(mList?.get(position)?.label)
            holder.itemView.btn.setText(
                if (!mList?.get(position)?.ansValue?.isNullOrEmpty()!!) mList?.get(
                    position
                )?.ansValue else Html.fromHtml(mList?.get(position)?.name)
            )
            holder.itemView.btn?.setOnClickListener {
                listener?.onClick(position, holder.itemView.btn, mList?.get(position))
            }
        } else {

        }
        if (position == mList?.size - 1) {
            isAllowedForListener = false
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun getItemCount(): Int {
        return mList?.size
    }

    override fun getItemViewType(position: Int): Int {
        if ((!mList?.get(position)?.id?.equals("location", true)!!) &&
            (mList?.get(position)?.fieldType?.equals(
                "text",
                true
            )!! || mList?.get(position)?.fieldType?.equals(
                "number",
                true
            )!!)) {
            return VIEWTYPE_TEXT
        }else if (mList?.get(position)?.fieldType?.equals(
                "textarea",
                true
            )!!
        ) {
            return VIEWTYPE_MULT_TEXT
        } else if ((mList?.get(position)?.id?.equals("location", true)!!) || mList?.get(position)?.fieldType?.equals("location", true)!! || mList?.get(
                position
            )?.fieldType?.equals("date", true)!!
        ) {
            return VIEWTYPE_BUTTON
        } else if (mList?.get(position)?.fieldType?.equals("checkbox", true)!!) {
            return VIEWTYPE_CHECKBOX
        } else if (mList?.get(position)?.fieldType?.equals("file", true)!!) {
            return VIEWTYPE_FILEPICKER
        } else if (mList?.get(position)?.fieldType?.equals("radio", true)!!) {
            return VIEWTYPE_RADIOBTN
        } else if (mList?.get(position)?.fieldType?.equals("select", true)!!) {
            return VIEWTYPE_DROPDOWN
        }
        return VIEWTYPE_FILEPICKER
    }

    fun onRefreshAdapter(list: ArrayList<CustomFieldObj>, pos: Int, flag: Boolean) {
        mList = list
        isAllowedForListener = flag
        notifyDataSetChanged()
//        notifyItemChanged(pos)
    }

    inner class TextViewHolder(var holderview: View) : RecyclerView.ViewHolder(holderview) {
        init {
            CommonUtils.printLog("POSITION", "${position}- ${layoutPosition} - ${adapterPosition}")
//            if (mList?.get(pos)?.fieldType?.equals("text")!! || mList?.get(pos)?.fieldType?.equals(
//                    "number"
//                )!!
//            ) {
                holderview.edt_custom_field.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if(mList?.get(adapterPosition)?.fieldType?.equals("text")!!){
                            CommonUtils.printLog(
                                "ANS_TXT_LISTNER_SINGLE ${adapterPosition}",
                                "${s.toString()}"
                            )
                            var customFieldObj = mList?.get(adapterPosition)
                            customFieldObj?.ansValue = s?.toString()
                            listener.onClick(
                                adapterPosition,
                                holderview.edt_custom_field,
                                customFieldObj
                            )
                        }
                    }
                })
                holderview.edt_custom_field_number.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        //                        if(!isAllowedForListener) {
                        if(mList?.get(adapterPosition)?.fieldType?.equals("number")!!){
                            CommonUtils.printLog(
                                "ANS_TXT_LISTNER_SINGLE ${adapterPosition}",
                                "${s.toString()}"
                            )
                            var customFieldObj = mList?.get(adapterPosition)
                            customFieldObj?.ansValue = s?.toString()
                            listener.onClick(
                                adapterPosition,
                                holderview.edt_custom_field,
                                customFieldObj
                            )
                        }
//                        notifyItemChanged(pos)
                        //                        }
                    }
                })
                /* holderview.edt_custom_field.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
                     CommonUtils.printLog("FOCUS_SINGLE","${hasFocus}")
                 })*/
//            } else {
//                holderview.edt_custom_field_mult.addTextChangedListener(object : TextWatcher {
//                    override fun afterTextChanged(s: Editable?) {
//                    }
//
//                    override fun beforeTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        count: Int,
//                        after: Int
//                    ) {
//                    }
//
//                    override fun onTextChanged(
//                        s: CharSequence?,
//                        start: Int,
//                        before: Int,
//                        count: Int
//                    ) {
//                        //                        if(!isAllowedForListener) {
//                        CommonUtils.printLog(
//                            "ANS_TXT_LISTNER_MULT ${pos}",
//                            "${s.toString()}"
//                        )
//                        var customFieldObj = mList?.get(pos)
//                        customFieldObj?.ansValue = s?.toString()
//                        listener.onClick(
//                            pos,
//                            holderview.edt_custom_field_mult,
//                            customFieldObj
//                        )
////                        notifyItemChanged(pos)
//                        //                        }
//                    }
//                })
//                /* holderview.edt_custom_field_mult.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
//                     CommonUtils.printLog("FOCUS_MULT","${hasFocus}")
//                 })*/
//            }

        }
        fun bind(context: Context?, customFieldObj: CustomFieldObj, pos:Int, listener: OnItemClickListener){
        }


        }

    inner class TextMultViewHolder(var holderview: View) : RecyclerView.ViewHolder(holderview) {
        init {
            holderview.edt_custom_field_mult.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
//                        if(!isAllowedForListener) {
                    CommonUtils.printLog("ANS_TXT_LISTNER_MULT ${adapterPosition}", "${s.toString()}")
                    var customFieldObj = mList?.get(adapterPosition)
                    customFieldObj?.ansValue = s?.toString()
                    listener.onClick(
                        adapterPosition,
                        holderview.edt_custom_field_mult,
                        customFieldObj
                    )
//                    notifyItemChanged(adapterPosition)
//                        }
                }
            })
        }
        fun bind(context: Context?, customFieldObj: CustomFieldObj, pos:Int, listener: OnItemClickListener){
        }
    }

    class DropdownViewHolder(var itemview: View) : RecyclerView.ViewHolder(itemview) {
        fun bind(
            context: Context?,
            customFieldObj: CustomFieldObj,
            pos: Int,
            listener: OnItemClickListener
        ) {
            var dropdownItemList = customFieldObj?.value
            val adapter = CustomeSpinnerAdapter(context!!, dropdownItemList)/*ArrayAdapter(
                context!!,
                android.R.layout.simple_spinner_item,
                dropdownItemList!!
            )*/
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            itemView.spn_opt.adapter = adapter
            itemview.spn_opt.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
//                        if(flag) {
                        CommonUtils.printLog(
                            "ANS_DROPDOWN_LISTENER",
                            "${dropdownItemList?.get(pos)} ${pos}"
                        )
//                        customFieldObj?.ansValue = dropdownItemList?.get(pos)?.value
                        listener?.onClick(
                            position,
                            itemView.spn_opt,
                            dropdownItemList?.get(pos)//customFieldObj
                        )
//                        }
                    }
                }
            if (!customFieldObj?.ansValue?.isNullOrEmpty()!!) {
                dropdownItemList?.forEachIndexed { index, s ->
                    if (s?.value?.trim()?.equals(customFieldObj?.ansValue?.trim(), true)!!) {
                        CommonUtils.printLog("ANS_DROPDOWN_CHECK", "${s} ${index}")
                        itemview.spn_opt.setSelection(index)
                    }
                }
            }
        }
    }

    class CheckboxViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class RadiobtnViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class FilePickerViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
