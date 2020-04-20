/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OpenGLEngine;
import GhostAtom.GhostAtom;
import GhostAtom.NICS_Scan;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;
/**
 *
 * @author djosh
 */
public class RenderNICSScan {
    
    public static void RenderScanGhostAtoms(NICS_Scan ScanToRender, Object GhastlyLock){
        GhostAtom SingleGhostAtom, BeginningAtom, EndingAtom;
        ArrayList<GhostAtom> GhostAtomListToRender;
        synchronized(GhastlyLock){
            // render as points if there are more than SCAN_NO_DISPLAY   
            if(ScanToRender.Number_Of_Points >= RenderSettings.SCAN_NO_DISPLAY){
                GhostAtomListToRender = ScanToRender.GetAtomList();
                
                if(GhostAtomListToRender == null) return;
                if(GhostAtomListToRender.isEmpty())return;       
                                
                // draw a line from the first atom to the last
                
                // get first and last atom
                BeginningAtom = GhostAtomListToRender.get(0);
                EndingAtom = GhostAtomListToRender.get(GhostAtomListToRender.size() - 1);
                // draw the line with the coordinates
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);                 
                GL11.glColor4f(RenderSettings.ScanCenterLineColor.x,
                               RenderSettings.ScanCenterLineColor.y,
                               RenderSettings.ScanCenterLineColor.z,
                               RenderSettings.ScanCenterLineColor.w);                
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(BeginningAtom.x, BeginningAtom.y, BeginningAtom.z);
                GL11.glVertex3f(EndingAtom.x, EndingAtom.y, EndingAtom.z);      
                GL11.glEnd();
                                                                                                                
                 // render the points as points -- low quality but maybe faster
                
                GL11.glBegin(GL11.GL_POINTS);             
                for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                    SingleGhostAtom = GhostAtomListToRender.get(itor);
                    if(SingleGhostAtom.HighlightLevel == 0){
                        GL11.glPointSize(RenderSettings.ScanPointSize);
                        GL11.glColor4f(RenderSettings.ScanGhostAtomColor.x,
                                       RenderSettings.ScanGhostAtomColor.y,
                                       RenderSettings.ScanGhostAtomColor.z,
                                       RenderSettings.ScanGhostAtomColor.w);                                        
                        GL11.glVertex3f(SingleGhostAtom.x,
                                        SingleGhostAtom.y,
                                        SingleGhostAtom.z);
                    }else if(SingleGhostAtom.HighlightLevel == 1){
                        GL11.glPointSize(RenderSettings.LargeScanPointSize);
                        GL11.glColor4f(RenderSettings.HighlightedScanAtomColor.x, 
                                       RenderSettings.ScanProposedCenterLineColor.y,
                                       RenderSettings.HighlightedScanAtomColor.z,
                                       RenderSettings.HighlightedScanAtomColor.w);                                        
                        GL11.glVertex3f(SingleGhostAtom.x,
                                        SingleGhostAtom.y,
                                        SingleGhostAtom.z);
                    }else{
                        GL11.glPointSize(RenderSettings.LargeScanPointSize);
                        GL11.glColor4f(RenderSettings.SelectedScanAtomColor.x, 
                                       RenderSettings.SelectedScanAtomColor.y,
                                       RenderSettings.SelectedScanAtomColor.z,
                                       RenderSettings.SelectedScanAtomColor.w);                                        
                        GL11.glVertex3f(SingleGhostAtom.x,
                                        SingleGhostAtom.y,
                                        SingleGhostAtom.z);
                    }
                        
                }
                GL11.glEnd ();                                                                                                               
                
            // render as spheres if there are less than SCAN_NO_DISPLAY    
            }else if(ScanToRender.Number_Of_Points < RenderSettings.SCAN_NO_DISPLAY){
                                                                               
                GhostAtomListToRender = ScanToRender.GetAtomList();
                
                if(GhostAtomListToRender == null) return;
                if(GhostAtomListToRender.isEmpty())return;       
                
                
                // draw a line from the first atom to the last
                
                // get first and last atom
                BeginningAtom = GhostAtomListToRender.get(0);
                EndingAtom = GhostAtomListToRender.get(GhostAtomListToRender.size() - 1);
                // draw the line with the coordinates
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);                 
                GL11.glColor4f(RenderSettings.ScanCenterLineColor.x,
                               RenderSettings.ScanCenterLineColor.y, 
                               RenderSettings.ScanCenterLineColor.z,
                               RenderSettings.ScanCenterLineColor.w);                
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(BeginningAtom.x, BeginningAtom.y, BeginningAtom.z);
                GL11.glVertex3f(EndingAtom.x, EndingAtom.y, EndingAtom.z);      
                GL11.glEnd();
                
                
                
                // draw all the spheres for the proposed NICS scan
                for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                    SingleGhostAtom = GhostAtomListToRender.get(itor);
                    GL11.glTranslatef(SingleGhostAtom.x, SingleGhostAtom.y, SingleGhostAtom.z);


                    // need to higlight atoms if they are
                    if(SingleGhostAtom.HighlightLevel == 0){
                        GL11.glColor4f(RenderSettings.ScanGhostAtomColor.x,
                                       RenderSettings.ScanGhostAtomColor.y,
                                       RenderSettings.ScanGhostAtomColor.z,
                                       RenderSettings.ScanGhostAtomColor.w);
                        GL11.glLoadName(itor + 10000);

                        Sphere s = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            s.draw(0.2f, 20, 20);
                        }else{
                            s.draw(SingleGhostAtom.radius, 20, 20);
                        }
                    }else if(SingleGhostAtom.HighlightLevel == 1){
                        GL11.glColor4f(RenderSettings.ScanGhostAtomColor.x,
                                       RenderSettings.ScanGhostAtomColor.y,
                                       RenderSettings.ScanGhostAtomColor.z,
                                       RenderSettings.ScanGhostAtomColor.w);
                        GL11.glLoadName(itor + 11000);
                        Sphere sin = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            sin.draw(0.2f, 20, 20);
                        }else{
                            sin.draw(SingleGhostAtom.radius, 20, 20);
                        } 
                        GL11.glColor4f(RenderSettings.HighlightedScanAtomColor.x,
                                       RenderSettings.HighlightedScanAtomColor.y,
                                       RenderSettings.HighlightedScanAtomColor.z,
                                       RenderSettings.HighlightedScanAtomColor.w);
                        GL11.glLoadName(itor + 10000);
                        Sphere sout = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            sout.draw(0.5f, 20, 20);
                        }else{
                            sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                        }
                    }else{
                        GL11.glColor4f(RenderSettings.ScanGhostAtomColor.x,
                                       RenderSettings.ScanGhostAtomColor.y,
                                       RenderSettings.ScanGhostAtomColor.z,
                                       RenderSettings.ScanGhostAtomColor.w);
                        GL11.glLoadName(itor + 11000);
                        Sphere sin = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            sin.draw(0.2f, 20, 20);
                        }else{
                            sin.draw(SingleGhostAtom.radius, 20, 20);
                        } 
                        GL11.glColor4f(RenderSettings.SelectedScanAtomColor.x,
                                       RenderSettings.SelectedScanAtomColor.y,
                                       RenderSettings.SelectedScanAtomColor.z,
                                       RenderSettings.SelectedScanAtomColor.w);
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void RenderScanProposedGhostAtoms(NICS_Scan ScanToRender, Object GhastlyLock){
        GhostAtom SingleGhostAtom, BeginningAtom, EndingAtom;
        ArrayList<GhostAtom> GhostAtomListToRender;
        synchronized(GhastlyLock){
            // render as points if there are more than SCAN_NO_DISPLAY   
            if(ScanToRender.Number_Of_Points >= RenderSettings.SCAN_NO_DISPLAY){
                GhostAtomListToRender = ScanToRender.GetAtomList();
                
                if(GhostAtomListToRender == null) return;
                if(GhostAtomListToRender.isEmpty())return;       
                                
                // draw a line from the first atom to the last
                
                // get first and last atom
                BeginningAtom = GhostAtomListToRender.get(0);
                EndingAtom = GhostAtomListToRender.get(GhostAtomListToRender.size() - 1);
                // draw the line with the coordinates
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);                 
                GL11.glColor4f(RenderSettings.ScanProposedCenterLineColor.x,
                               RenderSettings.ScanProposedCenterLineColor.y,
                               RenderSettings.ScanProposedCenterLineColor.z,
                               RenderSettings.ScanProposedCenterLineColor.w);                
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(BeginningAtom.x, BeginningAtom.y, BeginningAtom.z);
                GL11.glVertex3f(EndingAtom.x, EndingAtom.y, EndingAtom.z);      
                GL11.glEnd();
                                                                                                                
                 // render the points as points -- low quality but maybe faster
                
                GL11.glBegin(GL11.GL_POINTS);             
                for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                    SingleGhostAtom = GhostAtomListToRender.get(itor);
                    if(SingleGhostAtom.HighlightLevel == 0){
                        GL11.glPointSize(RenderSettings.ScanPointSize);
                        GL11.glColor4f(RenderSettings.ProposedScanGhostAtomColor.x,
                                       RenderSettings.ProposedScanGhostAtomColor.y,
                                       RenderSettings.ProposedScanGhostAtomColor.z,
                                       RenderSettings.ProposedScanGhostAtomColor.w);                                        
                        GL11.glVertex3f(SingleGhostAtom.x,
                                        SingleGhostAtom.y,
                                        SingleGhostAtom.z);
                    }else if(SingleGhostAtom.HighlightLevel == 1){
                        GL11.glPointSize(RenderSettings.LargeScanPointSize);
                        GL11.glColor4f(RenderSettings.HighlightedScanAtomColor.x, 
                                       RenderSettings.HighlightedScanAtomColor.y,
                                       RenderSettings.HighlightedScanAtomColor.z,
                                       RenderSettings.HighlightedScanAtomColor.w);                                        
                        GL11.glVertex3f(SingleGhostAtom.x,
                                        SingleGhostAtom.y,
                                        SingleGhostAtom.z);
                    }else{
                        GL11.glPointSize(RenderSettings.LargeScanPointSize);
                        GL11.glColor4f(RenderSettings.SelectedScanAtomColor.x, 
                                       RenderSettings.SelectedScanAtomColor.y,
                                       RenderSettings.SelectedScanAtomColor.z,
                                       RenderSettings.SelectedScanAtomColor.w);                                        
                        GL11.glVertex3f(SingleGhostAtom.x,
                                        SingleGhostAtom.y,
                                        SingleGhostAtom.z);
                    }
                        
                }
                GL11.glEnd ();                                                                                                               
                
            // render as spheres if there are less than SCAN_NO_DISPLAY    
            }else if(ScanToRender.Number_Of_Points < RenderSettings.SCAN_NO_DISPLAY){
                                                                               
                GhostAtomListToRender = ScanToRender.GetAtomList();
                
                if(GhostAtomListToRender == null) return;
                if(GhostAtomListToRender.isEmpty())return;       
                
                
                // draw a line from the first atom to the last
                
                // get first and last atom
                BeginningAtom = GhostAtomListToRender.get(0);
                EndingAtom = GhostAtomListToRender.get(GhostAtomListToRender.size() - 1);
                // draw the line with the coordinates
                GL11.glLineWidth(RenderSettings.OutlineLineWidth);                 
                GL11.glColor4f(RenderSettings.ScanProposedCenterLineColor.x,
                               RenderSettings.ScanProposedCenterLineColor.y, 
                               RenderSettings.ScanProposedCenterLineColor.z,
                               RenderSettings.ScanProposedCenterLineColor.w);                
                GL11.glBegin(GL11.GL_LINES);                                
                GL11.glVertex3f(BeginningAtom.x, BeginningAtom.y, BeginningAtom.z);
                GL11.glVertex3f(EndingAtom.x, EndingAtom.y, EndingAtom.z);      
                GL11.glEnd();
                
                
                
                // draw all the spheres for the proposed NICS scan
                for(int itor = 0; itor < GhostAtomListToRender.size(); itor++){
                    SingleGhostAtom = GhostAtomListToRender.get(itor);
                    GL11.glTranslatef(SingleGhostAtom.x, SingleGhostAtom.y, SingleGhostAtom.z);


                    // need to higlight atoms if they are
                    if(SingleGhostAtom.HighlightLevel == 0){
                        GL11.glColor4f(RenderSettings.ProposedScanGhostAtomColor.x,
                                       RenderSettings.ProposedScanGhostAtomColor.y,
                                       RenderSettings.ProposedScanGhostAtomColor.z,
                                       RenderSettings.ProposedScanGhostAtomColor.w);
                        GL11.glLoadName(itor + 10000);

                        Sphere s = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            s.draw(0.2f, 20, 20);
                        }else{
                            s.draw(SingleGhostAtom.radius, 20, 20);
                        }
                    }else if(SingleGhostAtom.HighlightLevel == 1){
                        GL11.glColor4f(RenderSettings.ProposedScanGhostAtomColor.x,
                                       RenderSettings.ProposedScanGhostAtomColor.y,
                                       RenderSettings.ProposedScanGhostAtomColor.z,
                                       RenderSettings.ProposedScanGhostAtomColor.w);
                        GL11.glLoadName(itor + 11000);
                        Sphere sin = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            sin.draw(0.2f, 20, 20);
                        }else{
                            sin.draw(SingleGhostAtom.radius, 20, 20);
                        } 
                        GL11.glColor4f(RenderSettings.HighlightedScanAtomColor.x,
                                       RenderSettings.HighlightedScanAtomColor.y,
                                       RenderSettings.HighlightedScanAtomColor.z,
                                       RenderSettings.HighlightedScanAtomColor.w);
                        GL11.glLoadName(itor + 10000);
                        Sphere sout = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            sout.draw(0.5f, 20, 20);
                        }else{
                            sout.draw(SingleGhostAtom.radius * 2.5f, 20, 20);
                        }
                    }else{
                        GL11.glColor4f(RenderSettings.ProposedScanGhostAtomColor.x,
                                       RenderSettings.ProposedScanGhostAtomColor.y,
                                       RenderSettings.ProposedScanGhostAtomColor.z,
                                       RenderSettings.ProposedScanGhostAtomColor.w);
                        GL11.glLoadName(itor + 11000);
                        Sphere sin = new Sphere();
                        //size adjustable
                        if(SingleGhostAtom.radius <= 0){
                            sin.draw(0.2f, 20, 20);
                        }else{
                            sin.draw(SingleGhostAtom.radius, 20, 20);
                        } 
                        GL11.glColor4f(RenderSettings.SelectedScanAtomColor.x,
                                       RenderSettings.SelectedScanAtomColor.y,
                                       RenderSettings.SelectedScanAtomColor.z,
                                       RenderSettings.SelectedScanAtomColor.w);
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
    
    
}
