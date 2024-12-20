package com.codework.movies_app.fragments.main_fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codework.movies_app.R
import com.codework.movies_app.adapters.FAQAdapter
import com.codework.movies_app.data.FAQItems
import com.codework.movies_app.databinding.FragmentFaqBinding
import com.codework.movies_app.databinding.FragmentHistoryBinding
import com.codework.movies_app.utils.VerticalItemDecoration

class FragmentFAQ: Fragment() {
    private lateinit var binding: FragmentFaqBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        binding.imgClose.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    val faqList = listOf(
        FAQItems(
            "Ứng dụng này có miễn phí hoàn toàn không?",
            "Ứng dụng cung cấp bản miễn phí với các tính năng cơ bản. Tuy nhiên, để sử dụng đầy đủ các công cụ nâng cao và loại bỏ quảng cáo, bạn cần đăng ký gói trả phí."
        ),
        FAQItems(
            "Tôi có thể chỉnh sửa video độ phân giải cao không?",
            "Ứng dụng hỗ trợ chỉnh sửa video với độ phân giải lên đến 4K. Tuy nhiên, để xuất file ở định dạng này, thiết bị của bạn cần đủ mạnh và có đủ dung lượng lưu trữ."
        ),
        FAQItems(
            "Ứng dụng có hỗ trợ thêm hiệu ứng chuyển cảnh không?",
            "Ứng dụng cung cấp một bộ sưu tập hiệu ứng chuyển cảnh phong phú, từ cơ bản đến nâng cao. Bạn có thể thêm, chỉnh sửa và tùy chỉnh thời gian chuyển cảnh theo ý muốn để tạo sự mượt mà cho video."
        ),
        FAQItems(
            "Tôi có thể thêm phụ đề hoặc văn bản vào video không?",
            "Bạn hoàn toàn có thể thêm phụ đề hoặc văn bản vào video. Ứng dụng cho phép tùy chỉnh kiểu chữ, màu sắc và hiệu ứng hiển thị để phù hợp với nội dung của bạn."
        ),
        FAQItems(
            "Ứng dụng có hỗ trợ lưu dự án để chỉnh sửa sau không?",
            "Ứng dụng có tính năng lưu dự án để bạn có thể quay lại chỉnh sửa bất cứ lúc nào. Tất cả các chỉnh sửa sẽ được lưu tạm thời trên thiết bị của bạn cho đến khi bạn xuất video."
        ),

        FAQItems(
            "Ứng dụng có hỗ trợ lưu dự án để chỉnh sửa sau không?",
            "Ứng dụng có tính năng lưu dự án để bạn có thể quay lại chỉnh sửa bất cứ lúc nào. Tất cả các chỉnh sửa sẽ được lưu tạm thời trên thiết bị của bạn cho đến khi bạn xuất video."
        ),
        FAQItems(
            "Tôi có thể xuất video với định dạng nào?",
            "Ứng dụng hỗ trợ xuất video ở nhiều định dạng phổ biến như MP4, MOV và AVI. Ngoài ra, bạn có thể tùy chỉnh chất lượng video từ HD đến 4K để phù hợp với nhu cầu sử dụng."
        ),
        FAQItems(
            "Ứng dụng có yêu cầu cấu hình phần cứng đặc biệt không?",
            "Để sử dụng mượt mà, ứng dụng yêu cầu thiết bị có ít nhất 2GB RAM và bộ xử lý đa lõi. Với video độ phân giải cao, bạn nên sử dụng thiết bị có cấu hình mạnh hơn để tránh bị giật, lag."
        ),
        FAQItems(
            "Làm thế nào để chia sẻ video sau khi xuất xong?",
            "Sau khi xuất video, bạn có thể chia sẻ trực tiếp qua các nền tảng như YouTube, Facebook, hoặc Instagram. Ứng dụng cung cấp nút chia sẻ nhanh để bạn không cần rời khỏi ứng dụng."
        ),
        FAQItems(
            "Ứng dụng có hỗ trợ thêm nhạc vào video không?",
            "Bạn có thể thêm nhạc từ thư viện cá nhân hoặc sử dụng các bản nhạc miễn phí được tích hợp trong ứng dụng. Ngoài ra, bạn cũng có thể điều chỉnh âm lượng và điểm bắt đầu của nhạc."
        ),
        FAQItems(
            "Ứng dụng có hỗ trợ tạo video từ hình ảnh không?",
            "Ứng dụng cho phép bạn tạo video từ các hình ảnh trong thư viện. Bạn có thể thêm hiệu ứng chuyển cảnh, nhạc nền và tùy chỉnh thứ tự hiển thị của hình ảnh."
        )
    )

    private fun setUpRv() {
        binding.rvQuestion.apply {
            adapter = FAQAdapter(faqList)
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false
            )

            addItemDecoration(VerticalItemDecoration())
        }
    }


}