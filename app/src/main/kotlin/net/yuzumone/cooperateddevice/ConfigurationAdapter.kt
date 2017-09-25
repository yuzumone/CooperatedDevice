package net.yuzumone.cooperateddevice

import android.content.Context
import android.databinding.DataBindingUtil
import android.net.wifi.WifiConfiguration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.yuzumone.cooperateddevice.databinding.ItemConfigurationBinding

class ConfigurationAdapter(context: Context) : ArrayAdapter<WifiConfiguration>(context, 0) {

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val binding: ItemConfigurationBinding
        if (view == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.item_configuration, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = view.tag as ItemConfigurationBinding
        }
        binding.configuration = getItem(position)
        return view!!
    }
}