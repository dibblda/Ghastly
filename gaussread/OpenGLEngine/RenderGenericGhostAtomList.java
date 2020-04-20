/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenGLEngine;

import GhostAtom.GhostAtom;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

/**
 *
 * @author djosh
 */
public class RenderGenericGhostAtomList {
    
    
    

    public static void RenderSimpleProposedGhostAtoms(ArrayList<GhostAtom> GhostAtomListToRender, Object GhastlyLock){
    GhostAtom SingleGhostAtom;
        synchronized(GhastlyLock){
            // in display proposed mode                                                      
                // only display proposed


            if(GhostAtomListToRender == null) return;
            if(GhostAtomListToRender.isEmpty())return;       
            for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                SingleGhostAtom = GhostAtomListToRender.get(itor);
                GL11.glTranslatef(SingleGhostAtom.x, SingleGhostAtom.y, SingleGhostAtom.z);


                // need to higlight atoms if they are
                if(SingleGhostAtom.HighlightLevel == 0){
                    GL11.glColor4f(RenderSettings.ProposedGhostAtomColor.x, RenderSettings.ProposedGhostAtomColor.y, RenderSettings.ProposedGhostAtomColor.z, RenderSettings.ProposedGhostAtomColor.w);
                    GL11.glLoadName(itor + 10000);

                    Sphere s = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        s.draw(0.2f, 20, 20);
                    }else{
                        s.draw(SingleGhostAtom.radius, 20, 20);
                    }
                }else if(SingleGhostAtom.HighlightLevel == 1){
                    GL11.glColor4f(RenderSettings.ProposedGhostAtomColor.x, RenderSettings.ProposedGhostAtomColor.y, RenderSettings.ProposedGhostAtomColor.z, RenderSettings.ProposedGhostAtomColor.w);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    } 
                    GL11.glColor4f(RenderSettings.HighlightedAtomColor.x, RenderSettings.HighlightedAtomColor.y, RenderSettings.HighlightedAtomColor.z, RenderSettings.HighlightedAtomColor.w);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }else{
                    GL11.glColor4f(RenderSettings.ProposedGhostAtomColor.x, RenderSettings.ProposedGhostAtomColor.y, RenderSettings.ProposedGhostAtomColor.z, RenderSettings.ProposedGhostAtomColor.w);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    } 
                    GL11.glColor4f(RenderSettings.SelectedAtomColor.x, RenderSettings.SelectedAtomColor.y, RenderSettings.SelectedAtomColor.z, RenderSettings.SelectedAtomColor.w);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }


                GL11.glTranslatef(-SingleGhostAtom.x, -SingleGhostAtom.y, -SingleGhostAtom.z);
            }

        }
    }


    public static void RenderSimpleGhostAtoms(ArrayList<GhostAtom> GhostAtomListToRender, Object GhastlyLock){
        GhostAtom SingleGhostAtom;
        synchronized(GhastlyLock){
            // in display proposed mode                                                      
            // only display proposed
            if(GhostAtomListToRender == null) return;
            if(GhostAtomListToRender.isEmpty())return;  

            for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                SingleGhostAtom = GhostAtomListToRender.get(itor);
                GL11.glTranslatef(SingleGhostAtom.x, SingleGhostAtom.y, SingleGhostAtom.z);


                // need to higlight atoms if they are
                if(SingleGhostAtom.HighlightLevel == 0){
                    GL11.glColor4f(RenderSettings.GhostAtomColor.x, RenderSettings.GhostAtomColor.y, RenderSettings.GhostAtomColor.z, RenderSettings.GhostAtomColor.w);
                    GL11.glLoadName(itor + 10000);
                    Sphere s = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        s.draw(0.2f, 20, 20);
                    }else{
                        s.draw(SingleGhostAtom.radius, 20, 20);
                    }
                }else if(SingleGhostAtom.HighlightLevel == 1){
                    GL11.glColor4f(RenderSettings.GhostAtomColor.x, RenderSettings.GhostAtomColor.y, RenderSettings.GhostAtomColor.z, RenderSettings.GhostAtomColor.w);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    }                                    
                    GL11.glColor4f(RenderSettings.HighlightedAtomColor.x, RenderSettings.HighlightedAtomColor.y, RenderSettings.HighlightedAtomColor.z, RenderSettings.HighlightedAtomColor.w);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }else{
                    GL11.glColor4f(RenderSettings.GhostAtomColor.x, RenderSettings.GhostAtomColor.y, RenderSettings.GhostAtomColor.z, RenderSettings.GhostAtomColor.w);
                    GL11.glLoadName(itor + 11000);
                    Sphere sin = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sin.draw(0.2f, 20, 20);
                    }else{
                        sin.draw(SingleGhostAtom.radius, 20, 20);
                    }
                    GL11.glColor4f(RenderSettings.SelectedAtomColor.x, RenderSettings.SelectedAtomColor.y, RenderSettings.SelectedAtomColor.z, RenderSettings.SelectedAtomColor.w);
                    GL11.glLoadName(itor + 10000);
                    Sphere sout = new Sphere();
                    //size adjustable
                    if(SingleGhostAtom.radius <= 0){
                        sout.draw(0.5f, 20, 20);
                    }else{
                        sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                    }
                }
                GL11.glTranslatef(-SingleGhostAtom.x, -SingleGhostAtom.y, -SingleGhostAtom.z);
            }
        }
    }



}
