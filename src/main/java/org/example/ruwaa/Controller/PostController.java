package org.example.ruwaa.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.DTOs.LearningContentDTO;
import org.example.ruwaa.DTOs.WorkPostDTO;
import org.example.ruwaa.Service.PostService;
import org.example.ruwaa.Stability.StabilityImageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.example.ruwaa.Stability.ImproveMode.UPSCALE_CREATIVE;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController
{
    private final PostService postService;
    private final StabilityImageClient stabilityImageClient;

//    @PostMapping("/create")
//    public ResponseEntity<?> postPost(@RequestBody @Valid Post post, Authentication auth){
//        System.out.println("\n\ncontroller:"+auth.getName()+"\n\n");
//       // mediaService.addPost(auth.getName(), post);
//        return ResponseEntity.status(200).body(new ApiResponse("Added"));
    //}
    //done
        @GetMapping("/get-all")
        public ResponseEntity<?> getAllPosts() {
          return   ResponseEntity.status(200).body(postService.getAll());
            }
    //done
    @GetMapping("/free-feed")
    public ResponseEntity<?> freeFeed() {
        return ResponseEntity.status(200).body(postService.freeFeed());
    }
    //done
    @GetMapping("/work-feed")
    public ResponseEntity<?> workFeed() {
        return ResponseEntity.status(200).body(postService.workFeed());
    }
    //done
    @GetMapping("/subscription-feed")
    public ResponseEntity<?> subscriptionFeed(Authentication auth) {
        return ResponseEntity.status(200).body(postService.subscribeFeed(auth.getName()));
    }

    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(Authentication auth) {
        return ResponseEntity.status(200).body( postService.getMyPost(auth.getName()) );
    }

    @GetMapping("/view/work/{postId}")
    public ResponseEntity<?> viewWorkPost(Authentication auth, @PathVariable Integer postId) {
        return   ResponseEntity.status(200).body(postService.viewWorkPost(auth.getName(), postId));
    }

    @GetMapping("/view/learning/{postId}")
    public ResponseEntity<?> viewLearningPost(Authentication auth, @PathVariable Integer postId) {
        return ResponseEntity.status(200).body(postService.viewLearningPost(auth.getName(), postId));
    }

    @PostMapping("/add/work")
    public ResponseEntity<?> addWorkPost(Authentication auth,@RequestPart("image") MultipartFile a, @RequestParam("content") String content, @RequestParam("isPublic") Boolean isPublic, @RequestParam("category")String category) throws IOException {
        System.out.println(a.getOriginalFilename());
            postService.addWorkPost(auth.getName(),a , content, isPublic, category);
            return   ResponseEntity.status(200).body(new ApiResponse("Work added :) ask for reviews!"));
    }

    @PostMapping("/add/learning")
    public ResponseEntity<?> addLearningContent(Authentication auth,@RequestParam("image") MultipartFile a, @RequestPart("content") String content, @RequestParam("isFree") Boolean isFree) throws IOException {
            postService.addLearningContent(auth.getName(),a, content, isFree);
        return   ResponseEntity.status(200).body(new ApiResponse("Learning content added :)"));

    }


    @PutMapping("/update/work/{postId}")
    public ResponseEntity<?> updateWorkPost(Authentication auth,@PathVariable Integer postId, @RequestBody WorkPostDTO dto) {
        postService.updateWorkPost(auth.getName(), postId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Work updated!"));

    }

    @PutMapping("/update/learning/{postId}")
    public ResponseEntity<?> updateLearningContent(Authentication auth, @PathVariable Integer postId, @RequestBody LearningContentDTO dto) {
        postService.updateLearningCont(auth.getName(), postId, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Learning content updated!"));

    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(Authentication auth, @PathVariable Integer postId) {
        postService.deletePost(auth.getName(), postId);
        return ResponseEntity.status(200).body(new ApiResponse("post removed"));

    }


    @PutMapping("/public/{postId}")
    public ResponseEntity<?> makePublic(Authentication auth,@PathVariable Integer postId) {
        postService.changeVisibilityToPublic(auth.getName(),postId);
        return ResponseEntity.status(200).body(new ApiResponse("Work Visibility switched"));
    }

    @PutMapping("/private/{postId}")
    public ResponseEntity<?> makePrivate(Authentication auth, @PathVariable Integer postId) {
        postService.changeVisibilityToPrivate(auth.getName(), postId);
        return ResponseEntity.status(200).body(new ApiResponse("Work Visibility switched"));
    }


    @GetMapping("/review/{post_id}")
    public ResponseEntity<?> reviewMyWork(Authentication auth,@PathVariable Integer post_id) {
        return ResponseEntity.status(200).body(postService.reviewMyWork(auth.getName(), post_id));
    }

    @PutMapping("/improve-attachment/{post_id}")
    public ResponseEntity<?> improveAttachment(@PathVariable Integer post_id) {
            return ResponseEntity.status(200).body(stabilityImageClient.upscale(post_id,UPSCALE_CREATIVE));
    }


}
