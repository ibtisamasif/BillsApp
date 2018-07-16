package com.codextech.ibtisam.bills_app.activities

import android.app.Dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.codextech.ibtisam.bills_app.R
import com.codextech.ibtisam.bills_app.adapters.BillerRecyclerAdapter
import com.codextech.ibtisam.bills_app.models.BPBiller
import com.codextech.ibtisam.bills_app.models.BPMerchant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.ArrayList
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    internal lateinit var adapter: BillerRecyclerAdapter
    internal var list: List<BPBiller> = ArrayList()
    private var selectedMerchantName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        fab.setOnClickListener { view ->
            addBiller()
        }

        recyclerView.setHasFixedSize(true)

        recyclerView.setLayoutManager(LinearLayoutManager(this))

        list = BPBiller.listAll(BPBiller::class.java)

        adapter = BillerRecyclerAdapter(list, this)

        recyclerView.setAdapter(adapter)

//        val bpUniversity: BPMerchant? = BPMerchant()
//        if (bpUniversity != null) {
//            bpUniversity.name = "UOL2"
//            bpUniversity.accountno = "112233445566"
//            bpUniversity.location = "Lahore2"
//            bpUniversity.save()
//            Toast.makeText(this, "University Added", Toast.LENGTH_SHORT).show()
//        }
//
//        val bpBiller: BPBiller? = BPBiller()
//        if (bpBiller != null) {
//            bpBiller.nickname = "ibtii"
//            bpBiller.referenceno = "bcs02121180"
//            bpBiller.university = bpUniversity
//            bpBiller.save()
//            Toast.makeText(this, "Biller Added: " + bpBiller.nickname, Toast.LENGTH_SHORT).show()
//        }
//
//        val bpTransation: BPTransac? = BPTransac()
//        if (bpTransation != null) {
//            bpTransation.transacName = "ibtiis money"
//            bpTransation.transacType = "credit"
//            bpTransation.transacBeneficiary = "mbeneficiary"
//            bpTransation.transacDate = "28 june"
//            bpTransation.save()
//            Toast.makeText(this, "Biller Added: " + bpTransation.transacName, Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_pay_bill -> {
                Toast.makeText(this, "Pay bill.", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_merchants -> {
                val intent = Intent(this, MerchantsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_transactions -> {
                val intent = Intent(this, TransactionsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_share -> {
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody = "Eid Mubarik cards app download now.https://play.google.com/store/apps/details?id=com.codextech.ibtisam.eidcard&hl=en"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share App")
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
            }
            R.id.nav_rateus -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.codextech.ibtisam.eidcard")))
                } catch (e: Exception) {
                    Toast.makeText(this, "Play store not found.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun addItemsOnSpinnerMerchants(dialog: Dialog) {
        val spMerchant = dialog.findViewById<Spinner>(R.id.spMerchant)
        var spinnerList = ArrayList<String>()
        spinnerList.add("Select Merchant")
        val list: List<BPMerchant> = BPMerchant.listAll(BPMerchant::class.java)
        for (j in 0 until list.size) {
            val oneMerchantName = list.get(j).name
            spinnerList.add(oneMerchantName)
        }
        val dataAdapter = ArrayAdapter(this, R.layout.spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spMerchant.setAdapter(dataAdapter)
        spMerchant.post(Runnable { spMerchant.setOnItemSelectedListener(MerchantSpinnerOnItemSelectedListener()) })
    }

    private inner class MerchantSpinnerOnItemSelectedListener : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
            val selectedMerchant = parent.getItemAtPosition(pos) as String
                    selectedMerchantName = selectedMerchant
                    Toast.makeText(parent.context, "Selected $selectedMerchant", Toast.LENGTH_SHORT).show()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}

    }

    private fun addBiller() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_biller)
        dialog.setCancelable(true)
        dialog.show()
        addItemsOnSpinnerMerchants(dialog)
        val bSave = dialog.findViewById<View>(R.id.bSave) as Button
        val bCancel = dialog.findViewById<View>(R.id.bCancel) as Button
        bCancel.setOnClickListener { dialog.dismiss() }
        bSave.setOnClickListener {
            val etName = dialog.findViewById<EditText>(R.id.etName)
            val etReference = dialog.findViewById<EditText>(R.id.etReference)
            val bpMerchant = BPMerchant.getMerchantByName(selectedMerchantName)
            if (etName.text.toString().isEmpty()) {
                etName.error = "Please enter nick name!"
            } else if (etReference.text.toString().isEmpty()) {
                etReference.error = "Please enter Reference number!"
            }else {
                if (bpMerchant != null) {
                    val biller = BPBiller()
                    biller.nickname = etName.text.toString()
                    biller.referenceno = etReference.text.toString()
                    biller.university = bpMerchant
                    if (biller.save() > 0) {
                        Toast.makeText(this, "Biller saved", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        list = BPBiller.listAll(BPBiller::class.java)
                        adapter.updateList(list)
                    } else {
                        Toast.makeText(this, "Error not saved something went wrong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Merchant no longer exists", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
