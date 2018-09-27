package com.kinglloy.album.view.component

import android.support.annotation.LayoutRes
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.CompoundButton
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem
import com.mikepenz.materialdrawer.model.BaseViewHolder

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
@Suppress("UNCHECKED_CAST")
abstract class MyAbstractSwitchDrawerItem<Item : MyAbstractSwitchDrawerItem<Item>>
    : BaseDescribeableDrawerItem<Item, MyAbstractSwitchDrawerItem.ViewHolder>() {

    private var switchEnabled = true

    private var checked = false
    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    fun withChecked(checked: Boolean): Item {
        this.checked = checked
        return this as Item
    }

    fun withSwitchEnabled(switchEnabled: Boolean): Item {
        this.switchEnabled = switchEnabled
        return this as Item
    }

    fun withOnCheckedChangeListener(onCheckedChangeListener: OnCheckedChangeListener): Item {
        this.onCheckedChangeListener = onCheckedChangeListener
        return this as Item
    }

    fun withCheckable(checkable: Boolean): Item {
        return withSelectable(checkable)
    }

    fun isChecked(): Boolean {
        return checked
    }

    fun isSwitchEnabled(): Boolean {
        return switchEnabled
    }

    fun getOnCheckedChangeListener(): OnCheckedChangeListener? {
        return onCheckedChangeListener
    }

    override fun getType(): Int {
        return com.mikepenz.materialdrawer.R.id.material_drawer_item_primary_switch
    }

    @LayoutRes
    override fun getLayoutRes(): Int {
        return com.mikepenz.materialdrawer.R.layout.material_drawer_item_switch
    }

    override fun bindView(viewHolder: ViewHolder, payloads: List<Any>) {
        super.bindView(viewHolder, payloads)

        //bind the basic view parts
        bindViewHelper(viewHolder)

        //handle the switch
        viewHolder.switchView.setOnCheckedChangeListener(null)
        viewHolder.switchView.isChecked = checked
        viewHolder.switchView.isEnabled = switchEnabled
        viewHolder.switchView.setOnCheckedChangeListener(checkedChangeListener)

        //add a onDrawerItemClickListener here to be able to check / uncheck if the drawerItem can't be selected
        withOnDrawerItemClickListener { _, _, _ ->
            if (!isSelectable) {
                checked = !checked
                viewHolder.switchView.isChecked = checked
            }

            true
        }

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, viewHolder.itemView)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder constructor(view: View) : BaseViewHolder(view) {
        val switchView: SwitchCompat = view.findViewById<View>(com.mikepenz.materialdrawer.R.id.material_drawer_switch) as SwitchCompat

    }

    private val checkedChangeListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isEnabled) {
                checked = isChecked
                if (getOnCheckedChangeListener() != null) {
                    getOnCheckedChangeListener()!!
                            .onCheckedChanged(this@MyAbstractSwitchDrawerItem, buttonView, isChecked)
                }
            } else {
                buttonView.setOnCheckedChangeListener(null)
                buttonView.isChecked = !isChecked
                buttonView.setOnCheckedChangeListener(this)
            }
        }
    }
}