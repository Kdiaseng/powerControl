package com.aplicativo.powercontrol

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aplicativo.powercontrol.adapter.DocumentAdapter
import com.aplicativo.powercontrol.database.AppDataBase
import com.aplicativo.powercontrol.dto.ElectricityBillDto
import com.aplicativo.powercontrol.utils.FileFacilitator
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_document_list.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class DocumentListFragment : Fragment(), DocumentAdapter.OnDocumentListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_document_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        FileFacilitator.init(requireContext())

        arguments?.let {
            val yearSelected = DocumentListFragmentArgs.fromBundle(it).yearSelected
            val documents = getDocuments(yearSelected)
            val years = DocumentListFragmentArgs.fromBundle(it).years
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item_year, years!!.toList())
            loadDropdown(adapter, yearSelected)
            loadListDocuments(documents)
        }
    }

    private fun loadDropdown(
        adapter: ArrayAdapter<Int>,
        yearSelected: Int
    ) {
        autoComplete_year.setAdapter(adapter)
        autoComplete_year.setText(yearSelected.toString(), false)
        autoComplete_year.setOnItemClickListener { parent, _, position, _ ->
            val documents = getDocuments(parent.getItemAtPosition(position) as Int)
            loadListDocuments(documents)
        }
    }

    private fun loadListDocuments(documents: ArrayList<ElectricityBillDto>) {
        recyclerView_docs.adapter = DocumentAdapter(documents, this)
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView_docs.layoutManager = layoutManager
    }

    override fun onClickDocument(document: ElectricityBillDto) {
        if(!FileFacilitator.openDocument(document.pathDocument))
            Snackbar.make(recyclerView_docs,getString(R.string.not_found), Snackbar.LENGTH_SHORT)
                .show()
    }

    private fun getDocuments(yearSelected: Int): ArrayList<ElectricityBillDto> {
        val list =
            AppDataBase(requireActivity()).electricityBillDao().getElectricityBillDtoAll(yearSelected)
        return ArrayList(list)
    }

}