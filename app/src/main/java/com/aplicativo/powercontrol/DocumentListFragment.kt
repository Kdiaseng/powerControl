package com.aplicativo.powercontrol

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.aplicativo.powercontrol.adapter.DocumentAdapter
import com.aplicativo.powercontrol.database.AppDataBase
import com.aplicativo.powercontrol.dto.ElectricityBillDto
import kotlinx.android.synthetic.main.fragment_document_list.*

/**
 * A simple [Fragment] subclass.
 */
class DocumentListFragment : Fragment(), DocumentAdapter.OnDocumentListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_document_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val documents = getDocuments()
        loadListDocuments(documents)
    }

    private fun loadListDocuments(documents: ArrayList<ElectricityBillDto>) {
        recyclerView_docs.adapter = DocumentAdapter(documents, this)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView_docs.layoutManager = layoutManager
    }

    override fun onClickDocument(document: ElectricityBillDto) {
        Log.e("CLICK", "lalalalal")
    }


    private fun getDocuments(): ArrayList<ElectricityBillDto> {
        val list =
            AppDataBase(requireActivity()).electricityBillDao().getElectricityBillDtoAll(2020)
        return ArrayList(list)

    }

}