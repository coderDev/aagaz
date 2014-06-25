package com.radaee.reader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import som.news.inapp.R;
import com.radaee.pdf.Document;
import com.radaee.pdf.Global;
import com.radaee.pdf.Page;
import com.radaee.pdf.Page.Annotation;
import com.radaee.reader.PDFReader.PDFReaderListener;
import com.radaee.util.PDFGridItem;
import com.radaee.util.PDFGridView;
import com.radaee.util.PDFThumbView;
import com.radaee.view.PDFVPage;
import com.radaee.view.PDFViewThumb.PDFThumbListener;

public class LibraryPDFReader extends Activity implements OnItemClickListener, OnClickListener, PDFReaderListener, PDFThumbListener
{
	private PDFGridView m_vFiles = null;
	private PDFReader m_reader = null;
	private PDFThumbView m_thumb = null;
	private RelativeLayout m_layout;
	private Document m_doc = new Document();

    private String str_find;
    private boolean m_set = false;
    private PDFVPage m_annot_vpage;
    private Annotation m_annot;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Global.Init( this );
        
		m_layout = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.mylib_reader, null);
		m_reader = (PDFReader)m_layout.findViewById(R.id.view);
		m_thumb = (PDFThumbView)m_layout.findViewById(R.id.thumbs);
		
		Bundle b = new Bundle();
		b = getIntent().getExtras();
		String PDF_URL = b.getString("PDF_URL");
		
		m_doc = new Document();
		
		m_doc.Open( PDF_URL, "" );
		m_reader.PDFOpen(m_doc, false, this);
		m_thumb.thumbOpen(m_reader.PDFGetDoc(), this);
		setContentView(m_layout);

        /*m_vFiles = new PDFGridView(this, null);
		m_vFiles.PDFSetRootPath("/mnt");
		m_vFiles.setOnItemClickListener(this);
		setContentView(m_vFiles);*/


        //invisible to make faster.
		//m_thumb.setVisibility(View.INVISIBLE);
		//bar_cmd.setVisibility(View.INVISIBLE);
		//bar_act.setVisibility(View.INVISIBLE);
		//bar_find.setVisibility(View.INVISIBLE);
    }
    protected void onDestroy()
    {
    	//m_vFiles.close();
    	if( m_vFiles != null )
    	{
    		m_vFiles.close();
    		m_vFiles = null;
    	}
    	if( m_thumb != null )
    	{
    		m_thumb.thumbClose();
    		m_thumb = null;
    	}
    	if( m_reader != null )
    		m_reader.PDFClose();
    	if( m_doc != null )
    		m_doc.Close();
    	Global.RemoveTmp();
    	super.onDestroy();
    }
    private void InputPassword(PDFGridItem item)
    {
		LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.dlg_password, null);
		final EditText tpassword = (EditText)layout.findViewById(R.id.txt_password);
		final PDFGridItem gitem = item;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which)
			{
				String password = tpassword.getText().toString();
				m_doc.Close();
				int ret = gitem.open_doc(m_doc, password);
				switch( ret )
				{
				case -1://need input password
					InputPassword(gitem);
					break;
				case -2://unknown encryption
					finish();
					break;
				case -3://damaged or invalid format
					finish();
					break;
				case -10://access denied or invalid file path
					finish();
					break;
				case 0://succeeded, and continue
					InitView();
					break;
				default://unknown error
					finish();
					break;
				}
			}});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}});
		builder.setTitle("Input Password");
		builder.setCancelable(false);
		builder.setView(layout);
		
		AlertDialog dlg = builder.create();
		dlg.show();
    }
    private class MyFontDelegate implements Document.PDFFontDelegate
    {

		public String GetExtFont(String collection, String fname, int flag,
				int[] ret_flags)
		{
			ret_flags[0] = 0;
			return null;
		}
    }
    private void InitView()
    {
		//m_doc.SetCache( Global.tmp_path + "/temp.dat" );//set temporary cache for editing.
		//test font delegate
		//m_doc.SetFontDel(new MyFontDelegate());
		//byte[] sign_contents = m_doc.GetSignContents();
		//if( sign_contents != null )
		//{
		//}
		
		m_reader.PDFOpen(m_doc, false, this);
		//m_reader.PDFGotoPage(10);
		m_thumb.thumbOpen(m_reader.PDFGetDoc(), this);
		setContentView(m_layout);
		
    }
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		if( arg0 == m_vFiles )
		{
			PDFGridItem item = (PDFGridItem)arg1;
			if( item.is_dir() )
			{
				m_vFiles.PDFGotoSubdir(item.get_name());
			}
			else
			{
				m_doc.Close();
				int ret = item.open_doc(m_doc, null);
				switch( ret )
				{
				case -1://need input password
					InputPassword(item);
					break;
				case -2://unknown encryption
					finish();
					break;
				case -3://damaged or invalid format
					finish();
					break;
				case -10://access denied or invalid file path
					finish();
					break;
				case 0://succeeded, and continue
					InitView();
					break;
				default://unknown error
					finish();
					break;
				}
			}
		}
		else
		{
		}
	}
	private void onSelect()
	{
		m_set = !m_set;
		m_reader.PDFSetSelect();
		
	}
	private void onInk()
	{
		m_set = !m_set;
		if( m_set )
			m_reader.PDFSetInk(0);
		else
			m_reader.PDFSetInk(1);
		
	}
	private void onRect()
	{
		m_set = !m_set;
		if( m_set )
			m_reader.PDFSetRect(0);
		else
			m_reader.PDFSetRect(1);
		
	}
	private void onOval()
	{
		m_set = !m_set;
		if( m_set )
			m_reader.PDFSetEllipse(0);
		else
			m_reader.PDFSetEllipse(1);
		
	}
	private void onNote()
	{
		m_reader.PDFSetNote();
		m_set = !m_set;
		
	}
	private void onLine()
	{
		m_set = !m_set;
		if( m_set )
			m_reader.PDFSetLine(0);
		else
			m_reader.PDFSetLine(1);
		
	}
	private void onCancel()
	{
		m_reader.PDFCancel();
		m_set = false;
	
	}
	private void onFindPrev()
	{
	/*	String str = txt_find.getText().toString();
		if( str_find != null )
		{
			if( str != null && str.compareTo(str_find) == 0 )
			{
				m_reader.PDFFind(-1);
				return;
			}
		}
		if( str != null && str.length() > 0 )
		{
			m_reader.PDFFindStart(str, false, false);
			m_reader.PDFFind(1);
			str_find = str;
		}*/
	}
	private void onFindNext()
	{
		/*String str = txt_find.getText().toString();
		if( str_find != null )
		{
			if( str != null && str.compareTo(str_find) == 0 )
			{
				m_reader.PDFFind(1);
				return;
			}
		}
		if( str != null && str.length() > 0 )
		{
			m_reader.PDFFindStart(str, false, false);
			m_reader.PDFFind(1);
			str_find = str;
		}*/
	}
	private void onEdit()
	{
		LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.dlg_note, null);
		final EditText subj = (EditText)layout.findViewById(R.id.txt_subj);
		final EditText content = (EditText)layout.findViewById(R.id.txt_content);
		Page page = null;
		if( m_annot_vpage != null ) page = m_annot_vpage.GetPage();
		if( page == null ) return;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which)
			{
				String str_subj = subj.getText().toString();
				String str_content = content.getText().toString();
				m_annot.SetPopupSubject(str_subj);
				m_annot.SetPopupText(str_content);
				dialog.dismiss();
				m_reader.PDFEndAnnot();
				m_set = false;
				
			}});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				m_reader.PDFEndAnnot();
				m_set = false;
			
			}});
		builder.setTitle("Note Content");
		builder.setCancelable(false);
		builder.setView(layout);
		
		subj.setText(m_annot.GetPopupSubject());
		content.setText(m_annot.GetPopupText());
		AlertDialog dlg = builder.create();
		dlg.show();
	}
	private void onAct()
	{
		m_reader.PDFPerformAnnot();
		m_set = false;
		
	}
	private void onRemove()
	{
		m_reader.PDFRemoveAnnot();
		m_set = false;
	
	}
	public void onClick(View v)
	{
		if( v.getId() == R.id.btn_ink )
			onInk();
		else if( v.getId() == R.id.btn_rect )
			onRect();
		else if( v.getId() == R.id.btn_oval )
			onOval();
		else if( v.getId() == R.id.btn_note )
			onNote();
		else if( v.getId() == R.id.btn_line )
			onLine();
		else if( v.getId() == R.id.btn_cancel )
			onCancel();
		else if( v.getId() == R.id.btn_save )
			m_reader.PDFSave();
		else if( v.getId() == R.id.btn_sel )
			onSelect();
		else if( v.getId() == R.id.btn_remove )
			onRemove();
		else if( v.getId() == R.id.btn_act )
			onAct();
		else if( v.getId() == R.id.btn_edit )
			onEdit();
		else if( v.getId() == R.id.btn_prev )
			onFindPrev();
		else if( v.getId() == R.id.btn_next )
			onFindNext();
		else if( v.getId() == R.id.btn_close )
		{
			m_thumb.thumbClose();
    		m_reader.PDFClose();
        	if( m_doc != null ) m_doc.Close();
    		str_find = null;
	    	setContentView(m_vFiles);
		}
	}
	public void OnPageClicked(int pageno)
	{
		m_reader.PDFGotoPage(pageno);
	}
	public void OnPageChanged(int pageno)
	{
		m_thumb.thumbGotoPage(pageno);
	}
	public void OnAnnotClicked(PDFVPage vpage, Annotation annot)
	{
		m_annot_vpage = vpage;
		m_annot = annot;

	}
	public void OnOpenURI(String uri)
	{
	}
	public void OnOpenMovie(String path)
	{
	}
	public void OnOpenSound(int[] paras, String path)
	{
	}
	public void OnOpenAttachment(String path)
	{
	}
	public void OnOpen3D(String path)
	{
	}
	public void OnSelectEnd(String text)
	{
		LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.dlg_text, null);
		final RadioGroup rad_group = (RadioGroup)layout.findViewById(R.id.rad_group);
		final String sel_text = text;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int which)
			{
				if( rad_group.getCheckedRadioButtonId() == R.id.rad_copy )
					Toast.makeText(LibraryPDFReader.this, "todo copy text:" + sel_text, Toast.LENGTH_SHORT).show();
				else if( m_reader.PDFCanSave() )
				{
					boolean ret = false;
			        if( rad_group.getCheckedRadioButtonId() == R.id.rad_copy )
			    	{
	                    Toast.makeText(LibraryPDFReader.this, "todo copy text:" + sel_text, Toast.LENGTH_SHORT).show();
	                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	                    android.content.ClipData clip = android.content.ClipData.newPlainText("Radaee", sel_text);
	                    clipboard.setPrimaryClip(clip);                    
			        }
			        else if( rad_group.getCheckedRadioButtonId() == R.id.rad_highlight )
						ret = m_reader.PDFSetSelMarkup(0);
					else if( rad_group.getCheckedRadioButtonId() == R.id.rad_underline )
						ret = m_reader.PDFSetSelMarkup(1);
					else if( rad_group.getCheckedRadioButtonId() == R.id.rad_strikeout )
						ret = m_reader.PDFSetSelMarkup(2);
					else if( rad_group.getCheckedRadioButtonId() == R.id.rad_squiggly )
						ret = m_reader.PDFSetSelMarkup(4);
					if( !ret )
						Toast.makeText(LibraryPDFReader.this, "add annotation failed!", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(LibraryPDFReader.this, "can't write or encrypted!", Toast.LENGTH_SHORT).show();
				onSelect();
				dialog.dismiss();
			}});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}});
		builder.setTitle("Process selected text");
		builder.setCancelable(false);
		builder.setView(layout);
		AlertDialog dlg = builder.create();
		dlg.show();
	}
	public void OnPageModified(int pageno)
	{
		m_thumb.thumbUpdatePage(pageno);
	}
}
